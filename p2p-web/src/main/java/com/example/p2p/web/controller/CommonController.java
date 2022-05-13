package com.example.p2p.web.controller;

import com.example.p2p.model.user.FinanceAccount;
import com.example.p2p.model.user.User;
import com.example.p2p.service.user.FinanceAccountService;
import com.example.util.ConstantsUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @类名 CommanController
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/5 17:27
 * @版本 1.0
 */

@RestController
public class CommonController {

    @DubboReference(interfaceClass = FinanceAccountService.class, version = "1.0.0", check = false, timeout = 15000)
    private FinanceAccountService financeAccountService;

    /**
     * 查询header中下拉列表的余额
     * @param session
     * @return
     */
    @RequestMapping(value = "/user/queryAvailableMoneyByPhone.do", method = RequestMethod.GET)
    public Double queryAvailableMoneyByPhone(HttpSession session) {
        String phone = ((User) session.getAttribute(ConstantsUtil.SESSION_USER)).getPhone();
        FinanceAccount financeAccount = financeAccountService.queryAvailableMoneyByPhone(phone);
        return financeAccount.getAvailableMoney();
    }
}