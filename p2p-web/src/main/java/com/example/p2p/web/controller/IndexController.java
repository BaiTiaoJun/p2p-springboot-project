package com.example.p2p.web.controller;

import com.example.p2p.model.loan.LoanInfo;
import com.example.p2p.service.loan.BidInfoService;
import com.example.p2p.service.loan.LoanInfoService;
import com.example.p2p.service.user.UserService;
import com.example.util.ConstantsUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @DubboReference(interfaceClass = LoanInfoService.class, version = "1.0.0", check = false, timeout = 15000)
    private LoanInfoService loanInfoService;

    @DubboReference(interfaceClass = UserService.class, version = "1.0.0", check = false, timeout = 15000)
    private UserService userService;

    @DubboReference(interfaceClass = BidInfoService.class, version = "1.0.0", check = false, timeout = 15000)
    private BidInfoService bidInfoService;

    @RequestMapping("/")
    public String index(Model model) {
//        查询历史平均年化收益率
        Double historyAvgRate = loanInfoService.queryHistoryAvgRate();
        model.addAttribute(ConstantsUtil.HISTORY_AVG_RATE, historyAvgRate);

//        查询平台总人数
        Integer allUsersCount = userService.queryAllUsersCount();
        model.addAttribute(ConstantsUtil.ALL_USERS_COUNT, allUsersCount);

//        查询总投资金额
        Double allBidMoney = bidInfoService.queryAllBidMoney();
        model.addAttribute(ConstantsUtil.ALL_Bid_MONEY, allBidMoney);

//        按照发布日期，根据产品类型和首页展示数量展示贷款信息
        Map<String, Object> map = new HashMap<>();
        List<LoanInfo> loanInfos;
//        按照发布日期，新手宝理财产品只展示第 1 个
        map.put("productType", ConstantsUtil.NOVICE_TREASURE_TYPE);
        map.put("currentPage", 0);
        map.put("pageSize", 1);
        loanInfos = loanInfoService.queryLoanInfoByProductType(map);
        model.addAttribute(ConstantsUtil.LOAN_INFO_LIST_NOVICE_TREASURE, loanInfos);

//        按照发布日期，优先类理财产品展示前 4 个
        map.put("productType", ConstantsUtil.PRIORITY_TYPE);
        map.put("currentPage", 0);
        map.put("pageSize", 4);
        loanInfos = loanInfoService.queryLoanInfoByProductType(map);
        model.addAttribute(ConstantsUtil.LOAN_INFO_LIST_PRIORITY, loanInfos);

//        按照发布日期，散标类理财产品展示前 8 个
        map.put("productType", ConstantsUtil.SCATTERED_TYPE);
        map.put("currentPage", 0);
        map.put("pageSize", 8);
        loanInfos = loanInfoService.queryLoanInfoByProductType(map);
        model.addAttribute(ConstantsUtil.LOAN_INFO_LIST_SCATTERED, loanInfos);

        return "index";
    }
}
