package com.staticanalyzer.staticanalyzer.controller;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.staticanalyzer.staticanalyzer.entities.Result;
import com.staticanalyzer.staticanalyzer.entities.User;
import com.staticanalyzer.staticanalyzer.mapper.UserMapper;
import com.staticanalyzer.staticanalyzer.security.JWTHelper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api("用户控制器")
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JWTHelper jwtHelper;

    private static String RegularExpressionUsername = "[0-9a-zA-Z_-]{2,8}";
    private static String RegularExpressionPassword = ".{8,20}";

    private boolean verify(User user) {
        Pattern patternUsername = Pattern.compile(RegularExpressionUsername);
        Matcher matcherUsername = patternUsername.matcher(user.getUsername());
        if (!matcherUsername.matches())
            return false;

        Pattern patternPassword = Pattern.compile(RegularExpressionPassword);
        Matcher matcherPassword = patternPassword.matcher(user.getPassword());
        if (!matcherPassword.matches())
            return false;

        return true;
    }

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result login(@RequestBody User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        User dataBaseUser = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));

        if (dataBaseUser == null)
            return new Result(Result.REJECTED, Map.of("msg", "登录失败，用户不存在"));

        if (!dataBaseUser.getPassword().equals(password))
            return new Result(Result.REJECTED, Map.of("msg", "登录失败，密码错误"));

        String token = jwtHelper.generate(dataBaseUser.getId());
        return new Result(Result.ACCEPTED, Map.of("user", dataBaseUser, "token", token));
    }

    @PostMapping("/user")
    @ApiOperation("用户注册")
    public Result add(@RequestBody User user) {
        if (!verify(user))
            return new Result(Result.REJECTED, Map.of("msg", "注册失败，格式错误"));

        if (userMapper.selectOne(new QueryWrapper<User>().eq("username", user.getUsername())) != null)
            return new Result(Result.REJECTED, Map.of("msg", "注册失败，用户重名"));

        userMapper.insert(user);
        String token = jwtHelper.generate(user.getId());
        return new Result(Result.ACCEPTED, Map.of("user", user, "token", token));
    }

    @GetMapping("/user/{id}")
    @ApiOperation("查询用户信息")
    public Result query(@PathVariable int id) {
        User dataBaseUser = userMapper.selectById(id);
        if (dataBaseUser == null)
            return new Result(Result.REJECTED, Map.of("msg", "查询失败，找不到用户"));

        return new Result(Result.ACCEPTED, Map.of("user", dataBaseUser));
    }

    @PutMapping("/user/{id}")
    @ApiOperation("修改用户密码")
    public Result update(@PathVariable int id, @RequestBody String password) {
        User dataBaseUser = userMapper.selectById(id);
        if (dataBaseUser == null)
            return new Result(Result.REJECTED, Map.of("msg", "更新失败，找不到用户"));

        dataBaseUser.setPassword(password);
        if (!verify(dataBaseUser))
            return new Result(Result.REJECTED, Map.of("msg", "更新失败，格式错误"));

        userMapper.updateById(dataBaseUser);
        return new Result(Result.ACCEPTED, Map.of("msg", "更新成功"));
    }
}
