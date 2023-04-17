package com.staticanalyzer.staticanalyzer.service;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.staticanalyzer.staticanalyzer.entity.User;
import com.staticanalyzer.staticanalyzer.mapper.UserMapper;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, User> redisTemplate;

    private Pattern patternUsername;
    private Pattern patternPassword;

    private static String CACHE_KEY_USER = "user:";

    public UserService(
            @Value("${user.min-username-length}") int minUsernameLength,
            @Value("${user.max-username-length}") int maxUsernameLength,
            @Value("${user.min-password-length}") int minPasswordLength,
            @Value("${user.max-password-length}") int maxPasswordLength) {
        String regexUsername = String.format("[0-9a-zA-Z_-]{%d,%d}", minUsernameLength, maxUsernameLength);
        String regexPassword = String.format(".{%d,%d}", minPasswordLength, maxPasswordLength);
        patternUsername = Pattern.compile(regexUsername);
        patternPassword = Pattern.compile(regexPassword);
    }

    public User findUserById(int userId) {
        ValueOperations<String, User> operations = redisTemplate.opsForValue();
        String keyId = CACHE_KEY_USER + userId;

        User cachedUser = operations.get(keyId);
        if (cachedUser == null) {
            User databaseUser = userMapper.selectById(userId);
            if (databaseUser == null)
                return null;

            operations.set(keyId, databaseUser, 1800000, TimeUnit.MILLISECONDS);
            return databaseUser;
        }
        return cachedUser;
    }

    public User findUserByName(String username) {
        ValueOperations<String, User> operations = redisTemplate.opsForValue();
        String keyName = CACHE_KEY_USER + username;

        User cachedUser = operations.get(keyName);
        if (cachedUser == null) {
            User databaseUser = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
            if (databaseUser == null)
                return null;

            operations.set(keyName, databaseUser, 1800000, TimeUnit.MILLISECONDS);
            return databaseUser;
        }
        return cachedUser;
    }

    public boolean verifyUsername(String username) {
        return patternUsername.matcher(username).matches();
    }

    public boolean verifyPassword(String password) {
        return patternPassword.matcher(password).matches();
    }

    public boolean verifyUser(User user) {
        return user != null && verifyUsername(user.getUsername()) &&
                verifyPassword(user.getPassword());
    }

    public void createUser(User user) {
        userMapper.insert(user);
    }

    public void updateUser(User user) {
        ValueOperations<String, User> operations = redisTemplate.opsForValue();
        String keyId = CACHE_KEY_USER + user.getId();
        String keyName = CACHE_KEY_USER + user.getUsername();

        operations.getAndDelete(keyName);
        operations.getAndDelete(keyId);
        userMapper.updateById(user);
    }
}
