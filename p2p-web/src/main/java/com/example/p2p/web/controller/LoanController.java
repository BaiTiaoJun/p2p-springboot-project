package com.example.p2p.web.controller;

import com.example.p2p.model.loan.BidInfo;
import com.example.p2p.model.loan.LoanInfo;
import com.example.p2p.model.user.FinanceAccount;
import com.example.p2p.model.user.User;
import com.example.p2p.service.loan.BidInfoService;
import com.example.p2p.service.loan.LoanInfoService;
import com.example.p2p.service.user.FinanceAccountService;
import com.example.util.CalculatePageUtil;
import com.example.util.ConstantsUtil;
import com.example.util.ReturnResult;
import com.example.vo.InvestTopVo;
import com.example.vo.ReturnVoByPage;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @类名 LoanController
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/1 22:42
 * @版本 1.0
 */
@Controller
public class LoanController {

    @DubboReference(interfaceClass = LoanInfoService.class, version = "1.0.0", check = false, timeout = 15000)
    private LoanInfoService loanInfoService;

    @DubboReference(interfaceClass = BidInfoService.class, version = "1.0.0", check = false, timeout = 15000)
    private BidInfoService bidInfoService;


    @DubboReference(interfaceClass = FinanceAccountService.class, version = "1.0.0", check = false, timeout = 15000)
    private FinanceAccountService financeAccountService;

    @RequestMapping("/loan/queryLoanInfosListByPageAndType.do")
    public String queryLoanInfosListByPageAndType(Model model,
                       @RequestParam(name = "ptype", required = false) String ptype,
                       @RequestParam(name = "currentPage", required = false, defaultValue = "1") Integer currentPage) {
        /**
         * 返回商品列表，每次显示9个
         * 返回每种类型商品的总条数
         */
        ReturnVoByPage<LoanInfo> returnVo = new ReturnVoByPage<>();

        Map<String, Object> map = new HashMap<>();
        if (ObjectUtils.allNotNull(ptype) && ObjectUtils.isNotEmpty(ptype)) {
            map.put("productType", ptype);
        }
        map.put("currentPage", currentPage - 1);
        map.put("pageSize", ConstantsUtil.PAGE_SIZE);

//          根据类型返回每页显示商品所有条数的信息
        List<LoanInfo> loanInfos = loanInfoService.queryLoanInfoByProductType(map);
//          根据类型返回所有商品的总条数
        Integer totalSize = loanInfoService.queryLoanInfoTotalSizeByProductType(map);

        returnVo.setList(loanInfos);
        returnVo.setTotalSize(totalSize);
        returnVo.setTotalPage(CalculatePageUtil.getTotalPage(totalSize));
        returnVo.setCurrentPage(currentPage);
        returnVo.setPType(ptype);

        model.addAttribute("returnVo", returnVo);

        //返回投资排行的记录
        List<InvestTopVo> investTopVos = loanInfoService.queryInvestTopList();
        model.addAttribute("investTopVos", investTopVos);

        return "loan/loan";
    }

    @RequestMapping("/loan/queryLoanInfoForDetailsById.do/{id}")
    public String queryLoanInfoForDetailsById(Model model, HttpSession session,
                                              @PathVariable(value = "id") Integer id) {
        /**
         * 返回这个id的产品详细信息
         * 当前这个产品的投资记录
         */
        LoanInfo loanInfo = loanInfoService.queryLoanInfoByProductId(id);

        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("currentPage", 0);
        map.put("pageSize", ConstantsUtil.PAGE_SIZE_FOR_BID_DETAIL);
        List<BidInfo> bidInfos = bidInfoService.queryBidInfoListByIdAndPage(map);

        model.addAttribute("loanInfo", loanInfo);
        model.addAttribute("bidInfos", bidInfos);

        User user = (User) session.getAttribute(ConstantsUtil.SESSION_USER);
        if (user != null) {
            String phone = user.getPhone();
            FinanceAccount financeAccount = financeAccountService.queryAvailableMoneyByPhone(phone);
            model.addAttribute("availableMoney", financeAccount.getAvailableMoney());
        }
        return "loan/loanInfo";
    }

    @PutMapping(value = "/loan/invest.do")
    public @ResponseBody ReturnResult invest(HttpSession session,
                                             @RequestParam("bidMoney") Double bidMoney,
                                             @RequestParam("loanId") Integer loanId) {
        User user = (User) session.getAttribute(ConstantsUtil.SESSION_USER);
        Map<String, Object> map = new HashMap<>();
        map.put("bidMoney", bidMoney);
        map.put("loanId", loanId);
        map.put("uId", user.getId());
        map.put("phone", user.getPhone());

        try {
            loanInfoService.invest(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.getResult(ConstantsUtil.FAIL_CODE, "投资失败，请稍后再试");
        }
        return ReturnResult.getResult(ConstantsUtil.SUCCESS_CODE, "");
    }
}