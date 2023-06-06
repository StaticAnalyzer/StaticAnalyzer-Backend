package com.staticanalyzer.staticanalyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.staticanalyzer.staticanalyzer.entity.Result;
import com.staticanalyzer.staticanalyzer.entity.user.User;
import com.staticanalyzer.staticanalyzer.entity.user.UserWithJwt;
import com.staticanalyzer.staticanalyzer.service.UserService;
import com.staticanalyzer.staticanalyzer.service.error.ServiceError;

@RestController
@Api(description = "用户控制器")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ApiOperation(value = "用户登录接口")
    public Result<UserWithJwt> login(@RequestBody User user) {
        try {
            User databaseUser = userService.login(user);
            String jws = userService.getSignature(databaseUser.getId());
            return Result.ok("登录成功", new UserWithJwt(databaseUser, jws));
        } catch (ServiceError serviceError) {
            return Result.error(serviceError.getMessage());
        }
    }

    @PostMapping("/user")
    @ApiOperation(value = "用户注册接口")
    public Result<UserWithJwt> create(@RequestBody User user) {
        try {
            userService.create(user);
            String jws = userService.getSignature(user.getId());
            return Result.ok("注册成功", new UserWithJwt(user, jws));
        } catch (ServiceError serviceError) {
            return Result.error(serviceError.getMessage());
        }
    }

    @GetMapping("/user/{uid}")
    @ApiOperation(value = "用户查询接口")
    public Result<User> read(@PathVariable("uid") int userId) {
        try {
            User databaseUser = userService.read(userId);
            return Result.ok("查询成功", databaseUser);
        } catch (ServiceError serviceError) {
            return Result.error(serviceError.getMessage());
        }
    }

    @PutMapping("/user/{uid}")
    @ApiOperation(value = "用户修改接口")
    public Result<?> update(@PathVariable("uid") int userId, @RequestBody String password) {
        try {
            User databaseUser = userService.read(userId);
            databaseUser.setPassword(password);
            userService.update(databaseUser);
            return Result.ok("修改成功");
        } catch (ServiceError serviceError) {
            return Result.error(serviceError.getMessage());
        }
    }

}
