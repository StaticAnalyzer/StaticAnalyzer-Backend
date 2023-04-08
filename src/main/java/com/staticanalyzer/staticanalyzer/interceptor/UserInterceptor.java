package com.staticanalyzer.staticanalyzer.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staticanalyzer.staticanalyzer.entities.Result;
import com.staticanalyzer.staticanalyzer.security.JWTHelper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("用户请求拦截器")
@Component
public class UserInterceptor implements HandlerInterceptor {
    @Autowired
    JWTHelper jwtHelper;

    @ApiOperation("token验证, Authorization Bearer")
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

        Result result = new Result(Result.AUTH_FAILED, Map.of("msg", "token验证失败"));
        String message = new ObjectMapper().writeValueAsString(result);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(message);
        return false;
    }
}
