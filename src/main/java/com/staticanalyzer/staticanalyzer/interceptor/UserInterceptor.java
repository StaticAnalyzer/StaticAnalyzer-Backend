package com.staticanalyzer.staticanalyzer.interceptor;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.staticanalyzer.staticanalyzer.security.JWTHelper;

import io.jsonwebtoken.Claims;

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
        Claims claims = jwtHelper.parse(token);

        if (claims == null)
            return false;

        Date now = new Date();
        if (now.after(claims.getExpiration()))
            return false;

        String requestURI = request.getRequestURI();
        String userId = requestURI.split("/")[3];
        if (claims.getSubject() != userId)
            return false;

        return true;
    }
}
