package com.staticanalyzer.staticanalyzer.controller;

import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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

    private static Logger logger = LoggerFactory.getLogger(UserController.class);
    private static String RegularExpressionUsername = "[0-9a-zA-Z_-]{2,8}";
    private static String RegularExpressionPassword = ".{8,20}";

    private boolean verify(User user) {
        Pattern patternPassword = Pattern.compile(RegularExpressionPassword);
        if (!patternPassword.matcher(user.getPassword()).matches())
            return false;

        Pattern patternUsername = Pattern.compile(RegularExpressionUsername);
        if (!patternUsername.matcher(user.getUsername()).matches())
            return false;

        return true;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        User dataBaseUser = userMapper.selectOne(new QueryWrapper<User>().eq("username", user.getPassword()));

        if (dataBaseUser == null || !dataBaseUser.getPassword().equals(user.getPassword())) {
            logger.info("Rejected login request made by " + user.getUsername());
            return Map.of("code", -1, "msg", "登录失败，用户不存在或密码错误");
        }

        logger.info("Accepted login request made by " + user.getUsername());
        return Map.of("code", 0, "user", dataBaseUser, "token", jwtHelper.generate(dataBaseUser.getId()));
    }

    @PostMapping("/user")
    public Map<String, Object> add(@RequestBody User user) {
        if (!verify(user)) {
            logger.info("Rejected login request made by " + user.getUsername());
            return Map.of("code", -1, "msg", "注册失败，格式错误");
        }

        if (userMapper.selectOne(new QueryWrapper<User>().eq("username", user.getUsername())) != null) {
            logger.info("Failed to add user " + user);
            return Map.of("code", -1, "msg", "注册失败，存在重名用户");
        }

        userMapper.insert(user);
        logger.info("Successfully added user " + user);
        return Map.of("code", 0, "user", user, "token", jwtHelper.generate(user.getId()));
    }

    @GetMapping("/user/{id}")
    public Map<String, Object> query(@PathVariable int id) {
        User dataBaseUser = userMapper.selectById(id);

        if (dataBaseUser == null) {
            logger.info("Rejected query request of id " + id);
            return Map.of("code", -1, "msg", "查询失败，找不到用户");
        }

        logger.info("Get user info of id " + id);
        return Map.of("code", 0, "user", dataBaseUser);
    }

    @PutMapping("/user/{id}")
    public Map<String, Object> update(@PathVariable int id, @Validated @RequestBody User user) {
        User dataBaseUser = userMapper.selectById(id);

        if (dataBaseUser == null || !dataBaseUser.getUsername().equals(user.getUsername())) {
            logger.info("Rejected update request of id " + id);
            return Map.of("code", -1, "msg", "更新用户信息失败，找不到用户或用户名称不匹配");
        }

        userMapper.updateById(user);
        logger.info("Updated user info of id " + id);
        return Map.of("code", 0, "msg", "更新用户信息成功");
    }
}
