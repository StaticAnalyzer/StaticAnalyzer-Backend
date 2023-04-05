package com.staticanalyzer.staticanalyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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

@RestController
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JWTHelper jwtHelper;

    @PostMapping("/login")
    public Result login(@Validated @RequestBody User user) {
        User userSaved = userMapper.selectOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        if (userSaved == null || !userSaved.getPassword().equals(userSaved.getPassword()))
            return new Result(Result.FAILURE).setField("msg", "auth failed");

        String token = jwtHelper.generate(userSaved.getId());
        return new Result(Result.SUCCESS)
                .setField("token", token)
                .setField("user", userSaved);
    }

    @PostMapping("/user")
    public Result add(@Validated @RequestBody User user) {
        User userSaved = userMapper.selectOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        if (userSaved != null)
            return new Result(Result.FAILURE).setField("msg", "dup username");

        userMapper.insert(user);
        String token = jwtHelper.generate(user.getId());
        return new Result(Result.SUCCESS)
                .setField("token", token)
                .setField("user", user);
    }

    @GetMapping("/user/{id}")
    public Result query(@PathVariable int id) {
        User userSaved = userMapper.selectById(id);
        if (userSaved == null)
            return new Result(Result.FAILURE).setField("msg", "unk id");

        return new Result(Result.SUCCESS).setField("user", userSaved);
    }

    @PutMapping("/user/{id}")
    public Result update(@PathVariable int id, @Validated @RequestBody User user) {
        User userSaved = userMapper.selectById(id);
        if (userSaved == null || !userSaved.getUsername().equals(user.getUsername()))
            return new Result(Result.FAILURE).setField("msg", "wrong username");

        userMapper.updateById(user);
        return new Result(Result.SUCCESS).setField("msg", "user updated");
    }
}
