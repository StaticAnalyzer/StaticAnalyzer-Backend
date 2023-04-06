package com.staticanalyzer.staticanalyzer.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.staticanalyzer.staticanalyzer.security.JWTHelper;

@Component
public class UserInterceptor implements HandlerInterceptor {
    @Autowired
    JWTHelper jwtHelper;

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            int tokenUserId = jwtHelper.parseId(header.substring(7));
            int requestUserId = Integer.parseInt(request.getRequestURI().split("/")[2]);
            if (tokenUserId == requestUserId)
                return true;
        }

        String message = new ObjectMapper().writeValueAsString(Map.of("code", -1, "msg", "token验证失败"));
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(message);
        return false;
    }
}
