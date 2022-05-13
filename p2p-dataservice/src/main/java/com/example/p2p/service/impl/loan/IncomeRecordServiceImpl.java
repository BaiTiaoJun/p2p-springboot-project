package com.example.p2p.service.impl.loan;

import com.example.p2p.mapper.loan.BidInfoMapper;
import com.example.p2p.mapper.loan.IncomeRecordMapper;
import com.example.p2p.mapper.loan.LoanInfoMapper;
import com.example.p2p.mapper.user.FinanceAccountMapper;
import com.example.p2p.model.loan.BidInfo;
import com.example.p2p.model.loan.IncomeRecord;
import com.example.p2p.model.loan.LoanInfo;
import com.example.p2p.model.user.FinanceAccount;
import com.example.p2p.service.loan.IncomeRecordService;
import com.example.util.ConstantsUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @类名 IncomeRecordServiceImpl
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/9 11:22
 * @版本 1.0
 */

@DubboService(interfaceClass = IncomeRecordService.class, version = "1.0.0", timeout = 15000)
public class IncomeRecordServiceImpl implements IncomeRecordService {

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Autowired
    private IncomeRecordMapper incomeRecordMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    /**
     * 1、查询产品的投资状态为1的产品信息，list<loaninfo>
     * 2、循环遍历已满标的产品
     * 3、根据已满标产品的id，获取当前（已满标）产品的投资记录，再遍历该产品的投资记录
     * 4、生成用户的收益记录（收益状态0是未返还、1是以返还）
     *           收益时间：产品满标时间 +投资周期（新手宝：天，优选、散标：月，通过产品类型判断）
     * 				日期类型和int类型不能直接相加，使用lang3工具包的DateUtils的addxxx方法，把date和int相加返回date类型
     *           收益金额：投资金额 * 日利率 * 周期（天）
     *              收益金额的返回值使用：Math.round(incomeMoney*Math.pow(10,2))/Math.pow(10,2);对小数格式化
     *
     * 5、将该产品的状态由1改为2
     */
    @Override
    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.REPEATABLE_READ,
            rollbackFor = Exception.class,
            readOnly = false
    )
    public void generateIncomeRecord() throws Exception{
        //查询产品的投资状态为1的产品信息，list<loaninfo>
        List<LoanInfo> loanInfos = loanInfoMapper.selectLoanInfoByProductStatus(ConstantsUtil.PRODUCT_STATUS_FULL);
        //循环遍历已满标的产品
        IncomeRecord incomeRecord;
        Date bidCycle;
        double incomeMoney, incomeMoneyTemp;
        for(LoanInfo loanInfo: loanInfos) {
            //根据已满标产品的id，获取当前（已满标）产品的投资记录，再遍历该产品的投资记录
            List<BidInfo> bidInfos = bidInfoMapper.selectBidInfoListByLoanId(loanInfo.getId());
            for (BidInfo bidInfo : bidInfos) {//生成用户的收益记录（收益状态0是未返还、1是以返还）
                incomeRecord = new IncomeRecord();
                incomeRecord.setLoanId(loanInfo.getId());
                incomeRecord.setUid(bidInfo.getUid());
                incomeRecord.setBidId(bidInfo.getId());
                incomeRecord.setBidMoney(bidInfo.getBidMoney());
                incomeRecord.setIncomeStatus(ConstantsUtil.INCOME_STATUS_NOT_BACK);
                /**
                 *  收益时间：产品满标时间 + 投资周期（新手宝：天，优选、散标：月，通过产品类型判断）
                 * 	日期类型和int类型不能直接相加，使用lang3工具包的DateUtils的addxxx方法，把date和int相加返回date类型
                 *
                 * 	收益金额：投资金额 * 日利率 * 周期（天）
                 *  收益金额的返回值使用：Math.round(incomeMoney*Math.pow(10,2))/Math.pow(10,2);对小数格式化
                 */
                if (loanInfo.getProductType() == ConstantsUtil.NOVICE_TREASURE_TYPE) {
                    bidCycle = DateUtils.addDays(loanInfo.getProductFullTime(), loanInfo.getCycle());
                    incomeMoneyTemp = bidInfo.getBidMoney() * (loanInfo.getRate() / 100 / 30) * loanInfo.getCycle() * 30;
                } else {
                    bidCycle = DateUtils.addMonths(loanInfo.getProductFullTime(), loanInfo.getCycle());
                    incomeMoneyTemp = bidInfo.getBidMoney() * (loanInfo.getRate() / 100 / 30) * loanInfo.getCycle();
                }
                incomeMoney = Math.round(incomeMoneyTemp * Math.pow(10, 2)) / Math.pow(10, 2);
                incomeRecord.setIncomeDate(bidCycle);
                incomeRecord.setIncomeMoney(incomeMoney);
                //向收益表添加一条记录
                int incomeRecordRow = incomeRecordMapper.insertIncomeRecord(incomeRecord);
                if (incomeRecordRow == 0) {
                    throw new Exception("生成收益表失败");
                }
            }
            //将该产品的状态由1改为2
            LoanInfo loanInfo1 = new LoanInfo();
            loanInfo1.setProductStatus(ConstantsUtil.PRODUCT_STATUS_FULL_GENERATE_REVENUE);
            loanInfo1.setId(loanInfo.getId());
            int loanInfoRow = loanInfoMapper.updateProductStatus(loanInfo1);
            if (loanInfoRow == 0) {
                throw new Exception("更改产品状态失败");
            }
        }
    }

    /**
     * 根据状态为0，查询出待返还收益的数据
     * 遍历待返还收益的记录
     * 根据遍历出的uid、bidmoney、incomemoney返回给相关用户的账户
     * 修改当前收益记录的状态
     */
    @Override
    @Transactional
    public void generateIncomeBackFinanceAccount() throws Exception{
        //根据状态为0，查询出待返还收益的数据
        List<IncomeRecord> incomeRecords = incomeRecordMapper.selectIncomeRecordsByIncomeStatusAndIncomeDate(ConstantsUtil.INCOME_STATUS_NOT_BACK);
        //遍历待返还收益的记录
        FinanceAccount financeAccount;
        for (IncomeRecord incomeRecord : incomeRecords) {
            //根据遍历出的uid、bidmoney、incomemoney返回给相关用户的账户
            financeAccount = new FinanceAccount();
            financeAccount.setUid(incomeRecord.getUid());
            financeAccount.setAvailableMoney(incomeRecord.getBidMoney() + incomeRecord.getIncomeMoney());

            int financeAccountRow = financeAccountMapper.updateAvailableMoneyByIncomeAndUId(financeAccount);
            if (financeAccountRow == 0) {
                throw new Exception("返还账户收益失败");
            }
            //修改当前收益记录的状态
            incomeRecord.setIncomeStatus(ConstantsUtil.INCOME_STATUS_BACK);
            int incomeRecordRow = incomeRecordMapper.updateByPrimaryKey(incomeRecord);
            if (incomeRecordRow == 0) {
                throw new Exception("修改当前收益记录的状态失败");
            }
        }
    }
}
