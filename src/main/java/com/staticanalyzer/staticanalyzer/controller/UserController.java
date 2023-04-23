package com.staticanalyzer.staticanalyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.staticanalyzer.staticanalyzer.entity.Response;
import com.staticanalyzer.staticanalyzer.entity.user.User;
import com.staticanalyzer.staticanalyzer.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(description = "用户认证信息")
class AuthData {

    @ApiModelProperty(value = "用户信息", required = true)
    private User user;

    @ApiModelProperty(value = "令牌", name = "jwt", required = true)
    private String token;
}

@RestController
@Api(description = "用户控制接口")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    public Response<AuthData> login(@RequestBody User user) {
        if (!userService.verify(user))
            return new Response<>(Response.ERROR, "用户格式错误");

        User databaseUser = userService.findByUsername(user.getUsername());
        if (databaseUser == null)
            return new Response<>(Response.ERROR, "找不到用户");

        if (!databaseUser.getPassword().equals(user.getPassword()))
            return new Response<>(Response.ERROR, "用户名或密码错误");

        String jws = userService.signById(databaseUser.getId());
        return new Response<>(Response.OK, "登录成功", new AuthData(databaseUser, jws));
    }

    @PostMapping("/user")
    @ApiOperation(value = "用户注册")
    public Response<AuthData> add(@RequestBody User user) {
        if (!userService.verify(user))
            return new Response<>(Response.ERROR, "用户格式错误");

        User databaseUser = userService.findByUsername(user.getUsername());
        if (databaseUser != null)
            return new Response<>(Response.ERROR, "用户名重复");

        userService.create(user);
        String jws = userService.signById(user.getId());
        return new Response<>(Response.OK, "注册成功", new AuthData(user, jws));
    }

    @GetMapping("/user/{uid}")
    @ApiOperation(value = "用户查询")
    public Response<User> query(@PathVariable("uid") int userId) {
        User databaseUser = userService.findById(userId);
        if (databaseUser == null)
            return new Response<>(Response.ERROR, "找不到用户");

        return new Response<>(Response.OK, "查询成功", databaseUser);
    }

    @PutMapping("/user/{uid}")
    @ApiOperation(value = "用户修改")
    public Response<?> update(@PathVariable("uid") int userId, @RequestBody String password) {
        User databaseUser = userService.findById(userId);
        if (databaseUser == null)
            return new Response<>(Response.ERROR, "找不到用户");

        databaseUser.setPassword(password);
        if (!userService.verify(databaseUser))
            return new Response<>(Response.ERROR, "密码格式错误");

        userService.update(databaseUser);
        return new Response<>(Response.OK, "更新成功");
    }
}
