package com.staticanalyzer.staticanalyzer.interceptor;

import java.io.IOException;
import java.nio.file.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.staticanalyzer.staticanalyzer.config.jwt.JwtProperties;
import com.staticanalyzer.staticanalyzer.entity.Result;
import com.staticanalyzer.staticanalyzer.utils.JwtUtils;

@Component
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    JwtProperties jwtProperties;

    private void setResponseMessage(HttpServletResponse response, String message) throws IOException {
        Result<?> result = Result.hint(message);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(new ObjectMapper().writeValueAsString(result));
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        String requestHeader = request.getHeader("Authorization");
        String jws = requestHeader.replaceFirst("Bearer ", "");
        int jwtUserId = -1;
        try {
            jwtUserId = JwtUtils.parseJws(jwtProperties.getKey(), jws);
        } catch (ExpiredJwtException expiredJwtException) {
            setResponseMessage(response, "身份认证过期，请重新登录");
            return false;
        } catch (JwtException jwtException) {
            setResponseMessage(response, "认证格式错误，请重新认证");
            return false;
        }

        Path requestPath = Path.of(request.getRequestURI());
        int requestUserId = Integer.parseInt(requestPath.getName(1).toString());
        if (jwtUserId != requestUserId) {
            setResponseMessage(response, "身份认证失败，请重新认证");
            return false;
        }
        return true;
    }
}
