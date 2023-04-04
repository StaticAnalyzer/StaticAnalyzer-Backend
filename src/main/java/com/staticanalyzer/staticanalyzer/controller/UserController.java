package com.staticanalyzer.staticanalyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.staticanalyzer.staticanalyzer.entities.Result;
import com.staticanalyzer.staticanalyzer.entities.User;
import com.staticanalyzer.staticanalyzer.mapper.UserMapper;
import com.staticanalyzer.staticanalyzer.security.JWTHelper;

@RestController
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JWTHelper jwtHelper;

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        User userSaved = userMapper.selectOne(new QueryWrapper<User>().eq("username", user.getUsername()));

        if (userSaved == null ||
                !userSaved.getPassword().equals(userSaved.getPassword())) {
            return Result.failure()
                    .setField("message", "auth failed");
        }

        String token = jwtHelper.generate(userSaved.getId());
        return Result.success()
                .setField("token", token)
                .setField("user", userSaved);
    }

    @PostMapping("/user")
    public Result add(@Validated @RequestBody User user) {
        User userSaved = userMapper.selectOne(
                new QueryWrapper<User>().eq("username", user.getUsername()));

        if (userSaved != null) {
            return Result.failure()
                    .setField("message", "dup username");
        }

        userMapper.insert(user);
        String token = jwtHelper.generate(user.getId());
        return Result.success()
                .setField("token", token)
                .setField("user", user);
    }

    @GetMapping("/user/{id}")
    public Result queryById(@PathVariable int id) {
        User userSaved = userMapper.selectById(id);

        if (userSaved == null) {
            return Result.failure()
                    .setField("message", "unk id");
        }

        return Result.success()
                .setField("user", userSaved);
    }

    @PutMapping("/user/{id}")
    public Result update(@PathVariable int id,
            @Validated @RequestBody User user) {
        if (user.getId() != id) {
            return Result.failure()
                    .setField("message", "id mismatched");
        }

        User userSaved = userMapper.selectById(id);

        if (userSaved == null) {
            return Result.failure()
                    .setField("message", "unk id");
        }

        userMapper.updateById(user);
        return Result.success()
                .setField("message", "user updated");
    }

}
