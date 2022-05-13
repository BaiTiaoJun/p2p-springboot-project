package com.example.p2p.service.impl.loan;

import com.example.p2p.mapper.loan.BidInfoMapper;
import com.example.p2p.mapper.loan.LoanInfoMapper;
import com.example.p2p.mapper.user.FinanceAccountMapper;
import com.example.p2p.model.loan.BidInfo;
import com.example.p2p.model.loan.LoanInfo;
import com.example.p2p.service.loan.LoanInfoService;
import com.example.util.ConstantsUtil;
import com.example.util.DateTimeUtil;
import com.example.vo.InvestTopVo;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@DubboService(interfaceClass = LoanInfoService.class, version = "1.0.0", timeout = 15000)
public class LoanInfoServiceImpl implements LoanInfoService {

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Autowired
    BidInfoMapper bidInfoMapper;

    @Override
    public Double queryHistoryAvgRate() {
//         设置redis序列化机制，key为string序列化,提高可读性
        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        获取redis操作对象
        ValueOperations<Object, Object> operations = redisTemplate.opsForValue();
//        获取redis中的数据
        Double historyAvgRate = (Double) operations.get(ConstantsUtil.HISTORY_AVG_RATE);
         /* 当第一次多个线程同时执行到这里，获取的redis中的数据都为null，他们都会去同步代码块
         * 后面的其他线程执行到这里的时候就不会去执行if语句中的同步代码块，可以直接从redis中取值
         * */
        if (!ObjectUtils.allNotNull(historyAvgRate)) {
//          高并发时，给当前对象(LoanInfoService对象)添加同步代码块，多个线程进行到这里的时候，每次只有一个线程能执行当前对象的这段代码，这样效率比同步方法高
            synchronized (this) {
             /* 高并发的时候当第一个线程进来时，redis中数据为空，继续执行下面的if语句中的代码块
             * 高并发的时候，由于第一个进来的线程已经执行了下面if中的语句，给redis赋值，所以后面的其他线程就直接从这里的redis取值不执行下面的if语句
             * */
                historyAvgRate = (Double) operations.get(ConstantsUtil.HISTORY_AVG_RATE);
                /* 当第一次从redis取数据的时候执行这里的语句
                * 如果redis中的数据为空就从mysql中取值，并赋值给redis
                * */
                if (!ObjectUtils.allNotNull(historyAvgRate)) {
                    historyAvgRate = loanInfoMapper.selectHistoryAvgRate();
                    operations.set(ConstantsUtil.HISTORY_AVG_RATE, historyAvgRate, 7, TimeUnit.HOURS);
                }
            }
        }
//        如果redis有数据，就直接取出返回，如果redis没有数据，就去mysql读取然后进行返回
        return historyAvgRate;
    }

    @Override
    public List<LoanInfo> queryLoanInfoByProductType(Map<String, Object> map) {
        return loanInfoMapper.selectLoanInfoByProductType(map);
    }

    @Override
    public Integer queryLoanInfoTotalSizeByProductType(Map<String, Object> map) {
        return loanInfoMapper.selectLoanInfoTotalSizeByProductType(map);
    }

    @Override
    public LoanInfo queryLoanInfoByProductId(Integer id) {
        return loanInfoMapper.selectByPrimaryKey(id);
    }

    /**
     *
     a) 更新产品剩余可投金额
     b) 更新账户金额
     c) 新增投资记录
     d) 更新产品状态
     e) 更新投资排行榜
     f) 更新平台总投资金额
     * @param map
     */
    @Override
    @Transactional(
            isolation = Isolation.DEFAULT,
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = Exception.class
    )
    public void invest(Map<String, Object> map) throws Exception {

        Integer loanId = (Integer) map.get("loanId");
        Double bidMoney = (Double) map.get("bidMoney");
        Integer uId = (Integer) map.get("uId");
        String phone = (String) map.get("phone");

        //获得乐观锁version状态值
        LoanInfo loanInfo = loanInfoMapper.selectVersionByLoanId(loanId);
        map.put("version", loanInfo.getVersion());

        //更新产品剩余可投金额
        int loanInfoLeftMoneyRes = loanInfoMapper.updateLeftProductMoneyByLoadId(map);
        if (loanInfoLeftMoneyRes == 0) {
            throw new Exception("更新产品可投剩余金额失败");
        }

        //更新账户金额
        int financeAccountRes = financeAccountMapper.updateAvailableMoneyByUId(map);
        if (financeAccountRes == 0) {
            throw new Exception("更新账户金额失败");
        }

        //新增投资记录
        BidInfo bidInfo = new BidInfo();
        bidInfo.setLoanId(loanId);
        bidInfo.setUid(uId);
        bidInfo.setBidMoney(bidMoney);
        bidInfo.setBidTime(DateTimeUtil.getFullDateTime(new Date()));
        bidInfo.setBidStatus(1);

        int bidInfoRes = bidInfoMapper.insert(bidInfo);
        if (bidInfoRes == 0) {
            throw new Exception("添加投资记录失败");
        }

        //更新产品状态，判断当前产品剩余可投金额是否为0，如果是，产品的状态由0改为1
        Double leftProductMoney = loanInfoMapper.selectLoanInfoLeftProductMoneyByLoanId(loanId);
        if (leftProductMoney == 0) {
            LoanInfo loanInfo1 = new LoanInfo();
            loanInfo1.setProductStatus(ConstantsUtil.PRODUCT_STATUS_FULL);
            loanInfo1.setProductFullTime(DateTimeUtil.getFullDateTime(new Date()));
            loanInfo1.setId(loanId);

            int loanInfoStatusRes = loanInfoMapper.updateProductStatus(loanInfo1);
            if (loanInfoStatusRes == 0) {
                throw new Exception("更新产品状态失败");
            }
        }

        //更新投资排行榜
        Double score = redisTemplate.opsForZSet().incrementScore(ConstantsUtil.INVEST_TOP, phone, bidMoney);
        System.out.println(score);

        //更新平台总投资金额
        Double allBidMoney = bidInfoMapper.selectAllBidMoney();
        if (allBidMoney != 0) {
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.opsForValue().set(ConstantsUtil.ALL_Bid_MONEY, allBidMoney, 7, TimeUnit.DAYS);
        } else {
            throw new Exception("更新平台总投资金额失败");
        }
    }

    /**
     * 生成投资排行榜
     * @return
     */
    @Override
    public List<InvestTopVo> queryInvestTopList() {
        List<InvestTopVo> investTopVos = new ArrayList<>();
        // 索引从0开始，左闭又开，通过指定key和索引区间，根据分数倒叙排列指定集合中指定区间的值，并返回一个set集合
        Set<ZSetOperations.TypedTuple<Object>> typedTuples =
                redisTemplate.opsForZSet().reverseRangeWithScores(ConstantsUtil.INVEST_TOP, 0, 5);
        if (ObjectUtils.allNotNull(typedTuples)) {
            //遍历set集合
            Iterator<ZSetOperations.TypedTuple<Object>> iterator = typedTuples.iterator();
            ZSetOperations.TypedTuple<Object> typedTuple;
            InvestTopVo investTopVo;
            while (iterator.hasNext()) {
                investTopVo = new InvestTopVo();
                typedTuple = iterator.next();
                investTopVo.setBidMoney(typedTuple.getScore());
                investTopVo.setPhone((String) typedTuple.getValue());
                investTopVos.add(investTopVo);
            }
        }
        return investTopVos;
    }
}