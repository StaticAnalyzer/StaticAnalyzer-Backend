package com.staticanalyzer.staticanalyzer.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.staticanalyzer.staticanalyzer.security.JWTHelper;

@Component
public class UserInterceptor implements HandlerInterceptor {
    @Autowired
    JWTHelper jwtHelper;

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader("Authorization");
        if (!header.startsWith("Bearer "))
            return false;

        String token = header.substring(7);
        if (jwtHelper.isExpired(token))
            return false;

        int tokenUserId = jwtHelper.parseId(token);
        int requestUserId = Integer.parseInt(request.getRequestURI().split("/")[2]);
        if (tokenUserId == -1 || tokenUserId != requestUserId)
            return false;

        return true;
    }
}
