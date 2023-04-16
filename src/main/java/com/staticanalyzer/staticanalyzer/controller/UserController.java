package com.staticanalyzer.staticanalyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.staticanalyzer.staticanalyzer.entity.User;
import com.staticanalyzer.staticanalyzer.mapper.UserMapper;
import com.staticanalyzer.staticanalyzer.model.RestSession;
import com.staticanalyzer.staticanalyzer.model.RestResult;
import com.staticanalyzer.staticanalyzer.utils.auth.AuthUtils;
import com.staticanalyzer.staticanalyzer.utils.auth.JwtUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(description = "用户控制接口")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthUtils authUtils;

    @PostMapping("/user")
    @ApiOperation(value = "用户注册")
    public RestResult<RestSession> add(@RequestBody User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        if (!authUtils.verifyUsername(username) || !authUtils.verifyPassword(password))
            return new RestResult<>(RestResult.ERROR, "用户名或密码格式错误");

        User databaseUser = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (databaseUser != null)
            return new RestResult<>(RestResult.ERROR, "用户名重复");

        userMapper.insert(user);
        String jws = jwtUtils.generateJws(user.getId());
        return new RestResult<>(RestResult.OK, "注册成功", new RestSession(user, jws));
    }

    @GetMapping("/user/{uid}")
    @ApiOperation(value = "用户查询")
    public RestResult<User> query(@PathVariable int uid) {
        User databaseUser = userMapper.selectById(uid);
        if (databaseUser == null)
            return new RestResult<>(RestResult.ERROR, "找不到用户");

        return new RestResult<>(RestResult.OK, "查询成功", databaseUser);
    }

    @PutMapping("/user/{uid}")
    @ApiOperation(value = "用户修改")
    public RestResult<?> update(@PathVariable int uid, @RequestBody String password) {
        User databaseUser = userMapper.selectById(uid);
        if (databaseUser == null)
            return new RestResult<>(RestResult.ERROR, "找不到用户");

        if (!authUtils.verifyPassword(password))
            return new RestResult<>(RestResult.ERROR, "密码格式错误");

        databaseUser.setPassword(password);
        userMapper.updateById(databaseUser);
        return new RestResult<>(RestResult.OK, "更新成功");
    }
}
