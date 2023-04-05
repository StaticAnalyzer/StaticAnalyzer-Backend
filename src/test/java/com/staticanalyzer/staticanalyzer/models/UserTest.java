package com.staticanalyzer.staticanalyzer.models;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.staticanalyzer.staticanalyzer.entities.User;
import com.staticanalyzer.staticanalyzer.mapper.UserMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@Rollback
public class UserTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void TestUserBasic() {
        System.out.println("TEST: GET ALL USERS");
        List<User> users = userMapper.selectList(new QueryWrapper<User>().orderByDesc("id"));
        System.out.println(users);

        System.out.println("TEST: INSERT USER");
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        userMapper.insert(user);
        User savedUser = userMapper.selectById(user.getId());
        System.out.println(savedUser);

        System.out.println("TEST: DUPLICATED USER");
        User dupUser = new User();
        dupUser.setUsername("test");
        dupUser.setPassword("654321");
        Assertions.assertThrows(Exception.class, () -> userMapper.insert(dupUser));
    }
}
