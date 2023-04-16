package com.staticanalyzer.staticanalyzer.interceptor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.staticanalyzer.staticanalyzer.model.RestResult;
import com.staticanalyzer.staticanalyzer.utils.auth.JwtUtils;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    JwtUtils jwtUtils;

    private String getJws(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (!header.startsWith("Bearer "))
            return null;
        return header.substring(7);
    }

    private int getUid(HttpServletRequest request) {
        return Integer.parseInt(request.getRequestURI().split("/")[2]);
    }

    private void setResponseMessage(HttpServletResponse response, String message) throws IOException {
        RestResult<?> result = new RestResult<>(RestResult.NO_AUTH, message);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(new ObjectMapper().writeValueAsString(result));
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        try {
            int jwtUserId = jwtUtils.parseJws(getJws(request));
            int requestUserId = getUid(request);
            if (jwtUserId != requestUserId) {
                setResponseMessage(response, "token认证失败");
                return false;
            }
            return true;
        } catch (ExpiredJwtException expiredJwtException) {
            setResponseMessage(response, "token过期");
            return false;
        } catch (Exception exception) {
            setResponseMessage(response, "token认证错误");
            return false;
        }
    }
}
