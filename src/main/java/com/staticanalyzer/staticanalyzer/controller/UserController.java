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
     * @see com.staticanalyzer.staticanalyzer.entity.user.User
     * @see com.staticanalyzer.staticanalyzer.entity.user.Identity
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录接口")
    public Result<Identity> login(@RequestBody User user) {
        if (!userService.check(user))
            return Result.error("用户名或密码格式错误");

        User databaseUser = userService.read(user.getUsername());
        if (databaseUser == null)
            return Result.error("找不到用户");

        if (!databaseUser.getPassword().equals(user.getPassword()))
            return Result.error("用户名或密码错误");

        String jws = userService.getSignature(databaseUser.getId());
        return Result.ok("登录成功", new Identity(databaseUser, jws));
    }

    /**
     * 用户注册接口
     * 
     * @apiNote 无需传递用户id
     * @param user
     * @return 出错时{@code data = null}
     * @see com.staticanalyzer.staticanalyzer.entity.user.User
     * @see com.staticanalyzer.staticanalyzer.entity.user.Identity
     */
    @PostMapping("/user")
    @ApiOperation(value = "用户注册接口")
    public Result<Identity> create(@RequestBody User user) {
        if (!userService.check(user))
            return Result.error("用户名或密码格式错误");

        User databaseUser = userService.read(user.getUsername());
        if (databaseUser != null)
            return Result.error("用户已存在");

        userService.create(user);
        String jws = userService.getSignature(user.getId());
        return Result.ok("注册成功", new Identity(user, jws));
    }

    /**
     * 用户查询接口
     * 
     * @param userId
     * @return 出错时{@code data = null}
     * @see com.staticanalyzer.staticanalyzer.entity.user.User
     */
    @GetMapping("/user/{uid}")
    @ApiOperation(value = "用户查询接口")
    public Result<User> read(@PathVariable("uid") int userId) {
        User databaseUser = userService.read(userId);
        if (databaseUser == null)
            return Result.error("找不到用户");
        return Result.ok("查询成功", databaseUser);
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
        User databaseUser = userService.read(userId);
        if (databaseUser == null)
            return Result.error("找不到用户");

        databaseUser.setPassword(password);
        if (!userService.check(databaseUser))
            return Result.error("用户名或密码格式错误");

        userService.update(databaseUser);
        return Result.ok("修改成功");
    }
}
