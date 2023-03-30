package com.staticanalyzer.staticanalyzer.models;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.staticanalyzer.staticanalyzer.entities.User;
import com.staticanalyzer.staticanalyzer.mapper.UserMapper;
import org.junit.Assert;
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
    public void TestUserBasic(){
        // 获取所有用户，按照id降序
        List<User> users = userMapper.selectList(new QueryWrapper<User>().orderByDesc("id"));
        System.out.println(users);
        // 测试正常插入，获取插入的id
        User user = new User();
        user.setUsername("test");
        user.setPassword("test");
        userMapper.insert(user);
        int id = user.getId();
        System.out.println(id);
        // 测试重名用户
        User dupUser = new User();
        dupUser.setUsername("test");
        dupUser.setPassword("test");
        Assertions.assertThrows(Exception.class, () -> userMapper.insert(dupUser));
    }
}
