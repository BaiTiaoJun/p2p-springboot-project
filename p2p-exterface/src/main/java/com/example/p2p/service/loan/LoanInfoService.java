package com.example.p2p.service.loan;

import com.example.p2p.model.loan.LoanInfo;
import com.example.vo.InvestTopVo;

import java.util.List;
import java.util.Map;

public interface LoanInfoService {
    /**
     * 查询历史平均年化收益率
     * @return
     */
    Double queryHistoryAvgRate();

    /**
     * 按照发布日期，根据产品类型和首页展示数量展示贷款信息
     * @param map
     * @return
     */
    List<LoanInfo> queryLoanInfoByProductType(Map<String, Object> map);

    /**
     * 根据类型返回所有商品的总条数
     * @param map
     * @return
     */
    Integer queryLoanInfoTotalSizeByProductType(Map<String, Object> map);

    /**
     * 根据id查询单条产品信息
     * @param id
     * @return
     */
    LoanInfo queryLoanInfoByProductId(Integer id);

    /**
     * 投资业务
     * @param map
     */
    void invest(Map<String, Object> map) throws Exception;

    /**
     * 从redis中查询投资排行榜
     * @return
     */
    List<InvestTopVo> queryInvestTopList();
}
