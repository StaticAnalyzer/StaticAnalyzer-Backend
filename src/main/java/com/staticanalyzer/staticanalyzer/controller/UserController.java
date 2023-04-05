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
import com.staticanalyzer.staticanalyzer.entities.ResultBuilder;
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
        if (userSaved == null || !userSaved.getPassword().equals(user.getPassword())) {
            return new ResultBuilder().setCode(Result.FAILURE).build();
        }

        String token = jwtHelper.generate(userSaved.getId());
        return new ResultBuilder().setCode(Result.SUCCESS)
                .addField("token", token)
                .addField("user", userSaved)
                .build();
    }

    @PostMapping("/user")
    public Result add(@Validated @RequestBody User user) {
        User userSaved = userMapper.selectOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        if (userSaved != null) {
            return new ResultBuilder().setCode(Result.FAILURE).build();
        }

        userMapper.insert(user);
        String token = jwtHelper.generate(user.getId());
        return new ResultBuilder().setCode(Result.SUCCESS)
                .addField("token", token)
                .addField("user", user)
                .build();
    }

    @GetMapping("/user/{id}")
    public Result query(@PathVariable int id) {
        User userSaved = userMapper.selectById(id);
        if (userSaved == null) {
            return new ResultBuilder().setCode(Result.FAILURE).build();
        }

        return new ResultBuilder().setCode(Result.SUCCESS)
                .addField("user", userSaved)
                .build();
    }

    @PutMapping("/user/{id}")
    public Result update(@PathVariable int id, @Validated @RequestBody User user) {
        User userSaved = userMapper.selectById(id);
        if (userSaved == null || !userSaved.getUsername().equals(user.getUsername())) {
            return new ResultBuilder().setCode(Result.FAILURE).build();
        }

        userMapper.updateById(user);
        return new ResultBuilder().setCode(Result.SUCCESS).build();
    }
}
