package com.example.p2p.config;

import com.example.p2p.web.interceptor.MyInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @类名 MyAppConfig
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/13 19:44
 * @版本 1.0
 */

@Configuration
public class MyAppConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        MyInterceptor myInterceptor = new MyInterceptor();
        String[] path = {"/commons/**", "/loan/**", "/recharge/**", "/system/**", "/user/**"};
        String[] excludePath = {"/user/toLoginPage.do", "/user/login.do",
                                "/loan/queryLoanInfosListByPageAndType.do",
                                "/loan/queryLoanInfoForDetailsById.do/{id}",
                                "/user/register.do", "/user/requestVerifyCode.do",
                                "/user/verifyPhone.do", "/user/toRegisterPage.do",
                                "/user/requestVerifyCodeImage.do"};

        InterceptorRegistration interceptorRegistration = registry.addInterceptor(myInterceptor);
        interceptorRegistration.addPathPatterns(path);
        interceptorRegistration.excludePathPatterns(excludePath);
    }
}
