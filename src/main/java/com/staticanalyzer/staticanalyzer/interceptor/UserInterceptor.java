package com.staticanalyzer.staticanalyzer.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.staticanalyzer.staticanalyzer.entity.Result;
import com.staticanalyzer.staticanalyzer.service.UserService;
import com.staticanalyzer.staticanalyzer.service.error.ServiceError;
import com.staticanalyzer.staticanalyzer.service.error.ServiceErrorType;

@Component
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    private void setResponseMessage(HttpServletResponse response, String message) throws java.io.IOException {
        Result<?> result = Result.hint(message);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(new ObjectMapper().writeValueAsString(result));
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        try {
            String requestHeader = request.getHeader("Authorization");
            String jws = requestHeader.replaceFirst("Bearer ", "");
            java.nio.file.Path requestPath = java.nio.file.Path.of(request.getRequestURI());
            int userId = Integer.parseInt(requestPath.getName(1).toString());
            userService.checkSignature(jws, userId); // 比较id
            return true;
        } catch (ServiceError serviceError) {
            setResponseMessage(response, serviceError.getMessage());
            return false;
        } catch (Exception exception) {
            setResponseMessage(response, ServiceErrorType.UNKNOWN.getMsg());
            return false;
        }
    }

}
