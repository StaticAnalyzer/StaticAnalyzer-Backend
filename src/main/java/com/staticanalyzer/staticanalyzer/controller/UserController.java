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
import com.staticanalyzer.staticanalyzer.entity.user.Identity;
import com.staticanalyzer.staticanalyzer.service.UserService;
import com.staticanalyzer.staticanalyzer.service.error.ServiceError;

/**
 * 用户控制器
 * 定义所有与用户相关的请求操作
 * 
 * @author iu_oi
 * @since 0.0.1
 */
@RestController
@Api(description = "用户控制器")
public class UserController {

    @Autowired /* 用户服务 */
    private UserService userService;

    /**
     * 用户登录接口
     * 
     * @apiNote 无需传递用户id
     * @param user
     * @return 出错时{@code data = null}
     * @see User
     * @see Identity
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录接口")
    public Result<Identity> login(@RequestBody User user) {
        try {
            User databaseUser = userService.login(user);
            String jws = userService.getSignature(databaseUser.getId());
            return Result.ok("登录成功", new Identity(databaseUser, jws));
        } catch (ServiceError serviceError) {
            return Result.error(serviceError.getMessage());
        }
    }

    /**
     * 用户注册接口
     * 
     * @apiNote 无需传递用户id
     * @param user
     * @return 出错时{@code data = null}
     * @see User
     * @see Identity
     */
    @PostMapping("/user")
    @ApiOperation(value = "用户注册接口")
    public Result<Identity> create(@RequestBody User user) {
        try {
            userService.create(user);
            String jws = userService.getSignature(user.getId());
            return Result.ok("注册成功", new Identity(user, jws));
        } catch (ServiceError serviceError) {
            return Result.error(serviceError.getMessage());
        }
    }

    /**
     * 用户查询接口
     * 
     * @param userId
     * @return 出错时{@code data = null}
     * @see User
     */
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

    /**
     * 用户修改接口
     * 
     * @apiNote 只支持修改密码
     * @param userId
     * @param password
     * @return {@code data = null}
     */
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
