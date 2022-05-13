package com.example.p2p.service.impl.loan;

import com.example.p2p.mapper.loan.BidInfoMapper;
import com.example.p2p.model.loan.BidInfo;
import com.example.p2p.service.loan.BidInfoService;
import com.example.util.ConstantsUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@DubboService(interfaceClass = BidInfoService.class, version = "1.0.0", timeout = 15000)
public class BidInfoServiceImpl implements BidInfoService {
    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public Double queryAllBidMoney() {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        ValueOperations<Object, Object> operations = redisTemplate.opsForValue();
        Double allBidMoney = (Double) operations.get(ConstantsUtil.ALL_Bid_MONEY);
        if (ObjectUtils.allNull(allBidMoney)) {
            synchronized (this) {
                allBidMoney = (Double) operations.get(ConstantsUtil.ALL_Bid_MONEY);
                if (ObjectUtils.allNull(allBidMoney)) {
                    allBidMoney = bidInfoMapper.selectAllBidMoney();
                    operations.set(ConstantsUtil.ALL_Bid_MONEY, allBidMoney, 7, TimeUnit.DAYS);
                }
            }
        }
        return allBidMoney;
    }

    @Override
    public List<BidInfo> queryBidInfoListByIdAndPage(Map<String, Object> map) {
        return bidInfoMapper.selectBidInfoListByIdAndPage(map);
    }
}
