package com.staticanalyzer.staticanalyzer.controller;

import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.staticanalyzer.staticanalyzer.entities.User;
import com.staticanalyzer.staticanalyzer.mapper.UserMapper;
import com.staticanalyzer.staticanalyzer.security.JWTHelper;

@RestController
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JWTHelper jwtHelper;

    private static String RegularExpressionUsername = "[0-9a-zA-Z_-]{2,8}";
    private static String RegularExpressionPassword = ".{8,20}";

    private boolean verify(User user) {
        Pattern patternPassword = Pattern.compile(RegularExpressionPassword);
        Pattern patternUsername = Pattern.compile(RegularExpressionUsername);
        if (!patternPassword.matcher(user.getPassword()).matches())
            return false;
        if (!patternUsername.matcher(user.getUsername()).matches())
            return false;
        return true;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        User dataBaseUser = userMapper.selectOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        if (dataBaseUser == null || !dataBaseUser.getPassword().equals(user.getPassword()))
            return Map.of("code", -1, "msg", "登录失败，用户不存在或密码错误");
        return Map.of("code", 0, "user", dataBaseUser, "token", jwtHelper.generate(dataBaseUser.getId()));
    }

    @PostMapping("/user")
    public Map<String, Object> add(@RequestBody User user) {
        if (!verify(user))
            return Map.of("code", -1, "msg", "注册失败，格式错误");
        if (userMapper.selectOne(new QueryWrapper<User>().eq("username", user.getUsername())) != null)
            return Map.of("code", -1, "msg", "注册失败，存在重名用户");
        userMapper.insert(user);
        return Map.of("code", 0, "user", user, "token", jwtHelper.generate(user.getId()));
    }

    @GetMapping("/user/{id}")
    public Map<String, Object> query(@PathVariable int id) {
        User dataBaseUser = userMapper.selectById(id);
        if (dataBaseUser == null)
            return Map.of("code", -1, "msg", "查询失败，找不到用户");
        return Map.of("code", 0, "user", dataBaseUser);
    }

    @PutMapping("/user/{id}")
    public Map<String, Object> update(@PathVariable int id, @RequestBody String password) {
        User dataBaseUser = userMapper.selectById(id);
        if (dataBaseUser == null)
            return Map.of("code", -1, "msg", "更新用户信息失败，找不到用户");
        dataBaseUser.setPassword(password);
        if (!verify(dataBaseUser))
            return Map.of("code", -1, "msg", "更新用户信息失败，格式错误");
        userMapper.updateById(dataBaseUser);
        return Map.of("code", 0, "msg", "更新用户信息成功");
    }
}
