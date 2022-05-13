package com.example.p2p.mapper.loan;

import com.example.p2p.model.loan.LoanInfo;

import java.util.List;
import java.util.Map;

public interface LoanInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LoanInfo record);

    int insertSelective(LoanInfo record);

    LoanInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LoanInfo record);

    int updateByPrimaryKey(LoanInfo record);

    /**
     * 查询历史平均年化收益率
     * @return
     */
    Double selectHistoryAvgRate();

    /**
     * 按照发布日期，根据产品类型和首页展示数量展示贷款信息
     * @param map
     * @return
     */
    List<LoanInfo> selectLoanInfoByProductType(Map<String, Object> map);

    /**
     * 根据类型返回所有商品的总条数
     * @return
     */
    Integer selectLoanInfoTotalSizeByProductType(Map<String, Object> map);

    /**
     * 根据产品id更新产品剩余可投金额
     * @param map
     * @return
     */
    int updateLeftProductMoneyByLoadId(Map<String, Object> map);

    /**
     * 根据loanId查询version状态值
     * @param loanId
     * @return
     */
    LoanInfo selectVersionByLoanId(Integer loanId);

    /**
     * 根据投标信息的id查询产品剩余可投金额
     * @param loanId
     * @return
     */
    Double selectLoanInfoLeftProductMoneyByLoanId(Integer loanId);

    /**
     * 根据产品未满标、满标、满标且生成收益表，更新产品状态信息和满标时间
     * @param loanInfo
     * @return
     */
    int updateProductStatus(LoanInfo loanInfo);

    /**
     * 查询产品状态为满标的产品信息
     * @return
     */
    List<LoanInfo> selectLoanInfoByProductStatus(int status);
}