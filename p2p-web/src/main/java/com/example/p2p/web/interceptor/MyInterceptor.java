package com.example.p2p.web.interceptor;

import com.example.p2p.model.user.User;
import com.example.util.ConstantsUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @类名 p2pInterceptor
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/13 19:42
 * @版本 1.0
 */
public class MyInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = (User) request.getSession().getAttribute(ConstantsUtil.SESSION_USER);
        if (ObjectUtils.allNull(user)) {
            response.sendRedirect(request.getContextPath());
            return false;
        }
        return true;
    }
}
