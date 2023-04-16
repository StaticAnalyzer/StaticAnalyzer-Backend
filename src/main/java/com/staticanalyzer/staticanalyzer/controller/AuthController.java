package com.staticanalyzer.staticanalyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.staticanalyzer.staticanalyzer.entity.User;
import com.staticanalyzer.staticanalyzer.mapper.UserMapper;
import com.staticanalyzer.staticanalyzer.model.RestSession;
import com.staticanalyzer.staticanalyzer.model.RestResult;
import com.staticanalyzer.staticanalyzer.utils.auth.AuthUtils;
import com.staticanalyzer.staticanalyzer.utils.auth.JwtUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(description = "用户认证接口")
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthUtils authUtils;

    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    public RestResult<RestSession> login(@RequestBody User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        if (!authUtils.verifyUsername(username) || !authUtils.verifyPassword(password))
            return new RestResult<>(RestResult.ERROR, "用户名或密码格式错误");

        User databaseUser = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (databaseUser == null)
            return new RestResult<>(RestResult.ERROR, "找不到用户");

        if (!databaseUser.getPassword().equals(password))
            return new RestResult<>(RestResult.ERROR, "用户名或密码错误");

        String jws = jwtUtils.generateJws(databaseUser.getId());
        return new RestResult<>(RestResult.OK, "登录成功", new RestSession(databaseUser, jws));
    }
}
