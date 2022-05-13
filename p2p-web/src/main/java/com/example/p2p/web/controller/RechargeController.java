package com.example.p2p.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.example.p2p.config.AlipayConfig;
import com.example.p2p.model.user.FinanceAccount;
import com.example.p2p.model.user.User;
import com.example.p2p.service.recharge.RechargeRecordService;
import com.example.p2p.service.user.FinanceAccountService;
import com.example.util.ConstantsUtil;
import com.example.util.TradeNoUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @类名 RechargeController
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/10 10:45
 * @版本 1.0
 */

@Controller
public class RechargeController {

    @DubboReference(interfaceClass = RechargeRecordService.class, version = "1.0.0", check = false, timeout = 15000)
    private RechargeRecordService rechargeRecordService;

    @DubboReference(interfaceClass = FinanceAccountService.class, version = "1.0.0", check = false, timeout = 15000)
    private FinanceAccountService financeAccountService;

    @RequestMapping("/recharge/toRecharge.do")
    public String toRecharge() {
        return "recharge/toRecharge";
    }

    @RequestMapping(value = "/recharge/pay.do", produces = "text/html;charset=utf-8")
    public @ResponseBody String pay(String rechargeMoney, HttpSession session) {
        User user = (User) session.getAttribute(ConstantsUtil.SESSION_USER);

        String out_trade_no = TradeNoUtil.generateTradeNo(); //商户订单号

        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl,
                AlipayConfig.app_id,
                AlipayConfig.merchant_private_key,
                AlipayConfig.format,
                AlipayConfig.charset,
                AlipayConfig.alipay_public_key,
                AlipayConfig.sign_type);

        //封装参数
        //订单信息
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", out_trade_no);//商户订单号，商户网站订单系统中唯一订单号，必填
        bizContent.put("total_amount", rechargeMoney);//付款金额，必填
        bizContent.put("subject", ConstantsUtil.ORDER_NAME);//订单名称，必填
        bizContent.put("body", ConstantsUtil.DESC_BODY);//商品描述，可空
        bizContent.put("product_code", ConstantsUtil.PRODUCT_CODE);//产品码
        //买家信息
        bizContent.put("timeout_express", ConstantsUtil.TIMEOUT_EXPRESS); //交易超时时间，超过这个时间交易失效
        bizContent.put("name", user.getName());//指定买家姓名
        bizContent.put("mobile", user.getPhone());//指定买家手机号
        /**
         * 指定买家证件类型
         * IDENTITY_CARD：身份证；
         * PASSPORT：护照；
         * OFFICER_CARD：军官证；
         * SOLDIER_CARD：士兵证；
         * HOKOU：户口本
         */
        bizContent.put("cert_type", ConstantsUtil.CERT_TYPE);//指定买家证件类型
        bizContent.put("cert_no", user.getIdCard());//买家证件号
        /**
         * 是否强制校验买家信息；
         * 需要强制校验传：T;
         * 不需要强制校验传：F或者不传；
         */
        bizContent.put("need_check_info", ConstantsUtil.NEED_CHECK_INFO);//是否强制校验买家信息

        //创建request,设置请求参数
        AlipayTradePagePayRequest PayRequest = new AlipayTradePagePayRequest();
        PayRequest.setNotifyUrl(AlipayConfig.notify_url); //设置异步回调地址
        PayRequest.setReturnUrl(AlipayConfig.return_url); //设置同步回调地址
        PayRequest.setBizContent(bizContent.toString());

        //返回请求表单
        String result = "";
        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(PayRequest);
            if (response.isSuccess()) {
                result = response.getBody();
                session.setAttribute("rechargeMoney", rechargeMoney);
                session.setAttribute("out_trade_no", out_trade_no);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @RequestMapping("/recharge/saveRechargeRecord.do")
    public String saveRechargeRecord(HttpServletRequest request) {
        HttpSession session = null;
        try {
            session = request.getSession();
            User user = (User) session.getAttribute(ConstantsUtil.SESSION_USER);
            String rechargeMoney = (String) session.getAttribute("rechargeMoney");
            String out_trade_no = (String) session.getAttribute("out_trade_no");
            Double availableMoney = rechargeRecordService.saveRechargeRecord(user, rechargeMoney, out_trade_no, ConstantsUtil.DESC_BODY);
            request.setAttribute("availableMoney", availableMoney);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.removeAttribute("rechargeMoney");
                session.removeAttribute("out_trade_no");
            }
        }
        return "user/myCenter";
    }
}
