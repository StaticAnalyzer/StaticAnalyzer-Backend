package com.staticanalyzer.staticanalyzer.interceptor;

import java.io.IOException;
import java.nio.file.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.staticanalyzer.staticanalyzer.config.UserConfig;
import com.staticanalyzer.staticanalyzer.entity.Response;
import com.staticanalyzer.staticanalyzer.utils.JwtUtils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

@Component
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    UserConfig userConfig;

    private void setResponseMessage(HttpServletResponse response, String message) throws IOException {
        Response<?> result = new Response<>(Response.NO_AUTH, message);
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
            jwtUserId = JwtUtils.parseJws(userConfig.getKey(), jws);
        } catch (ExpiredJwtException expiredJwtException) {
            setResponseMessage(response, "token过期");
            return false;
        } catch (JwtException jwtException) {
            setResponseMessage(response, "token格式错误");
            return false;
        }

        Path requestPath = Path.of(request.getRequestURI());
        int requestUserId = Integer.parseInt(requestPath.getName(1).toString());
        if (jwtUserId != requestUserId) {
            setResponseMessage(response, "token认证失败");
            return false;
        }
        return true;
    }
}
