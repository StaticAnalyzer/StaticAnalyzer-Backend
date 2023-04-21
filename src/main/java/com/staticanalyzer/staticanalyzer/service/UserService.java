package com.staticanalyzer.staticanalyzer.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.staticanalyzer.staticanalyzer.config.UserConfig;
import com.staticanalyzer.staticanalyzer.entity.user.User;
import com.staticanalyzer.staticanalyzer.mapper.UserMapper;
import com.staticanalyzer.staticanalyzer.utils.JwtUtils;

@Service
public class UserService {

    @Autowired
    private UserConfig userConfig;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, User> redisTemplate;

    private static String CACHE_KEY_USER = "user:";

    public boolean verify(User user) {
        return userConfig.getPasswordPattern().matcher(user.getPassword()).matches() &&
                userConfig.getUsernamePattern().matcher(user.getUsername()).matches();
    }

    public String signById(int userId) {
        return JwtUtils.generateJws(userConfig.getKey(), userConfig.getExpiration(), userId);
    }

    public User findById(int userId) {
        ValueOperations<String, User> operations = redisTemplate.opsForValue();
        String keyById = CACHE_KEY_USER + userId;

        User cachedUser = operations.get(keyById);
        if (cachedUser == null) {
            User databaseUser = userMapper.selectById(userId);
            operations.set(keyById, databaseUser, userConfig.getExpiration(), TimeUnit.MILLISECONDS);
            return databaseUser;
        }
        return cachedUser;
    }

    public User findByUsername(String username) {
        ValueOperations<String, User> operations = redisTemplate.opsForValue();
        String keyByUsername = CACHE_KEY_USER + username;

        User cachedUser = operations.get(keyByUsername);
        if (cachedUser == null) {
            User databaseUser = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
            operations.set(keyByUsername, databaseUser, userConfig.getExpiration(), TimeUnit.MILLISECONDS);
            return databaseUser;
        }
        return cachedUser;
    }

    public void create(User user) {
        userMapper.insert(user);
    }

    public void update(User user) {
        ValueOperations<String, User> operations = redisTemplate.opsForValue();
        String keyById = CACHE_KEY_USER + user.getId();
        String keyByUsername = CACHE_KEY_USER + user.getUsername();
        operations.getAndDelete(keyById);
        operations.getAndDelete(keyByUsername);
        userMapper.updateById(user);
    }
}
