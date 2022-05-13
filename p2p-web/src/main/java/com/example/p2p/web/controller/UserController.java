package com.example.p2p.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.p2p.model.user.FinanceAccount;
import com.example.p2p.model.user.User;
import com.example.p2p.service.RedisService;
import com.example.p2p.service.user.FinanceAccountService;
import com.example.p2p.service.user.UserService;
import com.example.util.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @类名 UserController
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/3 10:28
 * @版本 1.0
 */
@Controller
public class UserController {

    @DubboReference(interfaceClass = UserService.class, version = "1.0.0", check = false, timeout = 15000)
    private UserService userService;

    @DubboReference(interfaceClass = RedisService.class, version="1.0.0", check = false, timeout = 15000)
    private RedisService redisService;

    @DubboReference(interfaceClass = FinanceAccountService.class, version = "1.0.0", check = false, timeout = 15000)
    private FinanceAccountService financeAccountService;

    @RequestMapping("/user/toRegisterPage.do")
    public String toRegisterPage() {
        return "user/register";
    }

    @RequestMapping(value = "/user/verifyPhone.do", method = RequestMethod.GET)
    public @ResponseBody ReturnResult verifyPhone(@RequestParam("phone") String phone) {
        User user = userService.queryUserByPhone(phone);
        if (user != null) {
            return ReturnResult.getResult(ConstantsUtil.FAIL_CODE, "此用户已存在");
        } else {
            return ReturnResult.getResult(ConstantsUtil.SUCCESS_CODE, "");
        }
    }

    @RequestMapping(value = "/user/requestVerifyCode.do", method = RequestMethod.POST)
    public @ResponseBody ReturnResult requestVerifyCode(@RequestParam("pVal") String phone) {
        /**
         * 参数
         * url：请求的接口
         * path：请求的资源
         * method：请求方式
         */
        String url = "https://dfsns.market.alicloudapi.com";
        String path = "/data/send_sms";
        String method = "POST";

        /**
         * 参数：
         * Authorization：自己的AppCode 64ac953e7e79432fb2e6a92324132eb8
         */
        String appCodeStr = ConstantsUtil.APP_CODE;
        Map<String, String> appCode = new HashMap<>();
        appCode.put("Authorization", "APPCODE " + appCodeStr);

        /**
         * 参数：
         * phone_number：电话号码
         * content：验证码
         * template_id：短信文案模板
         */
        Map<String, String> params = new HashMap<>();
        String code = CodeUtil.getCode(6);
        params.put("phone_number", phone);
        params.put("content", "code:" + code);
        params.put("template_id", "TPL_0000");

        try {
            /**
             * 向sdk发送短信接口和请求参数，返回json字符串
             */
            HttpResponse httpResponse = HttpUtils.doPost(url, path, method, appCode, null, params);
            /**
             * 从response的body中获取json字符串,转换成json对象
             */
            JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(httpResponse.getEntity()));
            String status = jsonObject.getString("status");
            if (!StringUtils.equals(status, "OK")) {
                return ReturnResult.getResult(ConstantsUtil.FAIL_CODE, "发送验证码失败");
            } else {
                //使用注册后自动登录当前用户的phone作为短信验证码的key，把值暂存到redis中
                redisService.put(phone, code, 5);
                return ReturnResult.getResult(ConstantsUtil.SUCCESS_CODE, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.getResult(ConstantsUtil.FAIL_CODE, "发送验证码失败");
        }
    }

    @RequestMapping(value = "/user/register.do", method = RequestMethod.POST)
    public @ResponseBody ReturnResult register(HttpSession session,
                                                @RequestParam Map<String, Object> map) {
        String code = (String) map.get("code");
        String phone = (String) map.get("phone");

        if (StringUtils.equalsIgnoreCase(code, redisService.get(phone))) {
//            验证通后清空缓存
            redisService.remove(phone);
            try {
//            向用户个人信息表中保存注册的信息,向用户财务资金表中创建信息
                User user = userService.register(map);
//            注册成功后用户登录
                session.setAttribute(ConstantsUtil.SESSION_USER, user);
                return ReturnResult.getResult(ConstantsUtil.SUCCESS_CODE, "");
            } catch (Exception e) {
                e.printStackTrace();
                return ReturnResult.getResult(ConstantsUtil.FAIL_CODE, e.getMessage());
            }
        } else {
            if (ObjectUtils.allNull(redisService.get(phone))) {
                //验证后清空缓存
                redisService.remove(phone);
                return ReturnResult.getResult(ConstantsUtil.FAIL_CODE, "注册界面已过期，请刷新界面");
            } else {
                //验证后清空缓存
                redisService.remove(phone);
                return ReturnResult.getResult(ConstantsUtil.FAIL_CODE, "注册失败，请稍后再试");
            }
        }
    }

    @RequestMapping(value = "/user/toRealNamePage.do")
    public String toRealNamePage() {
        return "user/realName";
    }

    @RequestMapping(value = "/user/requestVerifyCodeImage.do")
    public @ResponseBody ReturnResult requestVerifyCodeImage(HttpSession session, @RequestParam(required = false, value = "phone") String phone) {
        String host = "http://ali-make-mark.showapi.com";
        String path = "/make-mark-img";
        String method = "GET";

        String appCodeStr = ConstantsUtil.APP_CODE;
        Map<String, String> appCode = new HashMap<>();
        appCode.put("Authorization", "APPCODE " + appCodeStr);

        Map<String, String> params = new HashMap<>();
        // 图片边框
        params.put("border", "no");
        // 图片宽高
        params.put("image_height", "38");
        params.put("image_width", "100");
        /**
         * 图片样式
         * 水纹 com.google.code.kaptcha.impl.WaterRipple
         * 鱼眼 com.google.code.kaptcha.impl.FishEyeGimpy
         * 阴影 com.google.code.kaptcha.impl.ShadowGimpy
         */
        params.put("obscurificator_impl", "com.google.code.kaptcha.impl.WaterRipple");
        // 字体长度
        params.put("textproducer_char_length", "4");
        // 字体间隔
        params.put("textproducer_char_space", "2");
        // 从此集合中生成图片文本
        params.put("textproducer_char_string", ConstantsUtil.TEXT_LIST);
        // 字体的颜色
        params.put("textproducer_font_color", "black");
        // 字体
        params.put("textproducer_font_names", "textproducer_font_names");
        // 字体大小
        params.put("textproducer_font_size", "30");

        String img_path = "";

        try {
            HttpResponse httpResponse = HttpUtils.doGet(host, path, method, appCode, params);
            /**
             * 返回的json字符串格式
             * {
             *    "showapi_res_error": "",
             *    "showapi_res_id": "62728fd70de3769f06121bc0",
             *    "showapi_res_code": 0,
             *    "showapi_fee_num": 1,
             *    "showapi_res_body": {"ret_code":0,"text":"Ayzp","img_path":"http://static1.showapi.com/app1/images/temp/20220504/bc0027b2-9371-4a53-a594-82c20d2b0938.jpg","img_path_https":"https://static1.showapi.com/app1/images/temp/20220504/bc0027b2-9371-4a53-a594-82c20d2b0938.jpg"}
             * }
             */
            //获取key为showapi_res_body中的json字符串
            JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(httpResponse.getEntity()));
            String res_body = jsonObject.getString("showapi_res_body");

            //获取showapi_res_body中的text和img_path
            JSONObject body = JSONObject.parseObject(res_body);
            img_path = body.getString("img_path");
            String text = body.getString("text");

            /**
             * 暂存一份到redis中，用于匹配验证用户输入的图片验证码信息
             * 如果是注册后获取验证码，使用注册后自动登录当前用户的id作为图片验证码的key
             * 如果是直接登录，生成使用用户的phone作为图片验证码的key
             */
            User user = (User) session.getAttribute(ConstantsUtil.SESSION_USER);
            if (ObjectUtils.allNotNull(user)) {
                String captchaKey = String.valueOf(user.getId());
                redisService.put(captchaKey, text, 1);
            } else {
                String loginCodeKey = UUIDUtil.getUUID();
                redisService.put(loginCodeKey, text, 1);
                session.setAttribute(ConstantsUtil.LOGIN_CODE_KEY, loginCodeKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnResult.getResult(ConstantsUtil.SUCCESS_CODE, img_path);
    }

    @RequestMapping(value = "/user/verifyRealName.do", method = RequestMethod.POST)
    public @ResponseBody ReturnResult verifyRealName(HttpSession session, @RequestParam Map<String, Object> map) {

        User user = (User) session.getAttribute(ConstantsUtil.SESSION_USER);

        //获取当前用户输入的验证码
        String captchaKey = String.valueOf(user.getId());
        String captcha = (String) map.get("captcha");
        //验证码不通过，不执行后面的验证
        if (!StringUtils.equalsIgnoreCase(captcha, redisService.get(captchaKey))) {
            //验证后清空缓存
            redisService.remove(captchaKey);
            return ReturnResult.getResult(ConstantsUtil.FAIL_CODE, "验证码错误，请重新输入");
        }

        //验证后清空缓存
        redisService.remove(captchaKey);

        //手机号码验证
        String phone = (String) map.get("phone");
        User userPhone = userService.queryUserByPhone(phone);
        if (userPhone == null) {
            return ReturnResult.getResult(ConstantsUtil.FAIL_CODE, "此手机号未注册，请重新输入");
        }

        //身份证号验证
        //请求的接口和方式
        String host = "http://checkone.market.alicloudapi.com";
        String path = "/chinadatapay/1882";
        String method = "POST";

        //最后在header中的格式(中间是英文空格)为Authorization, APPCODE:自己的appcode
        String appCode = ConstantsUtil.APP_CODE;
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + appCode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        //请求参数，同时验证姓名和身份证号码
        String idCard = (String) map.get("idCard");
        String realName = (String) map.get("realName");
        Map<String, String> params = new HashMap<>();
        params.put("idcard", idCard);
        params.put("name", realName);

        try {
            /**
             * {
             *  成功返回的参数
             *   "code": "10000",
             *   "message": "成功",
             *   "data": {
             *     "result": "1"
             *   },
             *   "seqNo": "4XU29Z4D1704061618"
             * }
             */
            HttpResponse httpResponse = HttpUtils.doPost(host, path, method, headers, null, params);

            JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(httpResponse.getEntity()));
            String code = jsonObject.getString("code");
            if (!StringUtils.equals(code, "10000")) {
                return ReturnResult.getResult(ConstantsUtil.FAIL_CODE, "验证失败，请重试");
            }

            String data = jsonObject.getString("data");
            JSONObject dataObject = JSONObject.parseObject(data);
            String result = dataObject.getString("result");

            if (!StringUtils.equals("1", result)) {
                return ReturnResult.getResult(ConstantsUtil.FAIL_CODE, "身份证号码或姓名有误，请重新验证");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.getResult(ConstantsUtil.FAIL_CODE, "验证失败，请重试");
        }

        //验证通过后，更新user用户的信息
        try {
            user.setIdCard(idCard);
            user.setName(realName);
            int res = userService.editUserByPhone(user);
            //验证通过并添加信息成功后
            if (res != 0) {
                return ReturnResult.getResult(ConstantsUtil.SUCCESS_CODE, "");
            } else {
                return ReturnResult.getResult(ConstantsUtil.FAIL_CODE, "验证失败，请重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.getResult(ConstantsUtil.FAIL_CODE, "验证失败，请重试");
        }
    }

    @RequestMapping("/user/loginOut.do")
    public String loginOut(HttpSession session) {
        /**
         *  removeAttribute：清空session中指定的k-v
         *  invalidate：销毁session作用域
         */
        //        session.removeAttribute(ConstantsUtil.SESSION_USER);

        session.invalidate();
        return "redirect:/index";
    }

    @RequestMapping("/user/toLoginPage.do")
    public String toLoginPage() {
        return "/user/login";
    }

    @RequestMapping(value = "/user/login.do", method = RequestMethod.POST)
    public @ResponseBody ReturnResult login(@RequestParam Map<String, Object> map, HttpSession session) {
        /**
         * 如果验证码正确可以进行帐号和密码的匹配
         */
        String captcha = (String) map.get("captcha");
        String loginCodeKey = (String) session.getAttribute(ConstantsUtil.LOGIN_CODE_KEY);
        String loginCode = redisService.get(loginCodeKey);
        if (StringUtils.equalsIgnoreCase(captcha, loginCode)) {

            //验证过后清空缓存和session中的验证码
            redisService.remove(loginCodeKey);
            session.removeAttribute(ConstantsUtil.LOGIN_CODE_KEY);

            User user = null;
            try {
                user = userService.login(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ObjectUtils.allNotNull(user)) {
                /**
                 * 如果帐号和密码能够查询出此用户，就登录此用户
                 */
                session.setAttribute(ConstantsUtil.SESSION_USER, user);
                return ReturnResult.getResult(ConstantsUtil.SUCCESS_CODE, "");
            } else {
                return ReturnResult.getResult(ConstantsUtil.FAIL_CODE, "帐号或密码，请稍后再试");
            }
        } else {
            //验证过后清空缓存和session中的验证码
            redisService.remove(loginCodeKey);
            session.removeAttribute(ConstantsUtil.LOGIN_CODE_KEY);
            return ReturnResult.getResult(ConstantsUtil.FAIL_CODE, "验证码错误，请重新输入");
        }
    }

    @RequestMapping("/user/toMyCenterPage.do")
    public String toMyCenterPage(HttpSession session, Model model) {
        if (session != null) {
            String phone = ((User) session.getAttribute(ConstantsUtil.SESSION_USER)).getPhone();
            FinanceAccount financeAccount = financeAccountService.queryAvailableMoneyByPhone(phone);
            model.addAttribute("availableMoney", financeAccount.getAvailableMoney());
        }
        return "user/myCenter";
    }

    @RequestMapping("/user/toMyInvestPage.do")
    public String toMyInvestPage() {
        return "user/myInvest";
    }

    @RequestMapping("/user/toMyRechargePage.do")
    public String toMyRechargePage() {
        return "user/myRecharge";
    }

    @RequestMapping("/user/toMyIncomePage.do")
    public String toMyIncomePage() {
        return "user/myIncome";
    }
}
