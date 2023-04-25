package com.staticanalyzer.staticanalyzer.interceptor;

import java.io.IOException;
import java.nio.file.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.staticanalyzer.staticanalyzer.entity.Result;
import com.staticanalyzer.staticanalyzer.service.UserService;

/**
 * 用户拦截器
 * 
 * @author iu_oi
 * @since 0.0.1
 */
@Component
public class UserInterceptor implements HandlerInterceptor {

    @Autowired /* 用户服务 */
    UserService userService;

    /**
     * 强行设置返回消息
     * 
     * @param response
     * @param message
     * @throws IOException
     */
    private void setResponseMessage(HttpServletResponse response, String message) throws IOException {
        Result<?> result = Result.hint(message);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(new ObjectMapper().writeValueAsString(result));
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        String requestHeader = request.getHeader("Authorization");
        if (requestHeader == null) {
            setResponseMessage(response, "认证格式错误，需要登录");
            return false;
        }
        /* 比较id */
        String jws = requestHeader.replaceFirst("Bearer ", "");
        Path requestPath = Path.of(request.getRequestURI());
        int userId = Integer.parseInt(requestPath.getName(1).toString());
        if (!userService.verifySignature(jws, userId)) {
            setResponseMessage(response, "身份认证失败，请重新登录");
            return false;
        }
        return true;
    }
}
