package com.example.p2p.service.loan;

import com.example.p2p.model.loan.BidInfo;

import java.util.List;
import java.util.Map;

public interface BidInfoService {
    /**
     * 查询总投资金额
     * @return
     */
    Double queryAllBidMoney();

    /**
     * 根据id查询当前这个产品的投资记录
     * @param map
     * @return
     */
    List<BidInfo> queryBidInfoListByIdAndPage(Map<String, Object> map);
}
