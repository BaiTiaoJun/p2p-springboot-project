package com.example.p2p.mapper.loan;

import com.example.p2p.model.loan.BidInfo;

import java.util.List;
import java.util.Map;

public interface BidInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BidInfo record);

    int insertSelective(BidInfo record);

    BidInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BidInfo record);

    int updateByPrimaryKey(BidInfo record);

    /**
     * 查询总投资金额
     * @return
     */
    Double selectAllBidMoney();

    /**
     * 根据id查询当前这个产品的投资记录
     * @param map
     * @return
     */
    List<BidInfo> selectBidInfoListByIdAndPage(Map<String, Object> map);

    /**
     * 根据已满标产品的id，查询产品投资记录
     * @param loanId
     * @return
     */
    List<BidInfo> selectBidInfoListByLoanId(int loanId);
}