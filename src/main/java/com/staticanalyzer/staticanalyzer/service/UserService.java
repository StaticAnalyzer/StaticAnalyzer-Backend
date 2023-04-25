package com.staticanalyzer.staticanalyzer.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.staticanalyzer.staticanalyzer.config.jwt.JwtProperties;
import com.staticanalyzer.staticanalyzer.config.user.UserProperties;
import com.staticanalyzer.staticanalyzer.entity.user.User;
import com.staticanalyzer.staticanalyzer.mapper.UserMapper;
import com.staticanalyzer.staticanalyzer.service.error.ServiceError;
import com.staticanalyzer.staticanalyzer.service.error.ServiceErrorType;
import com.staticanalyzer.staticanalyzer.utils.JwtUtils;

/**
 * 用户服务
 * 增删改查以及验证等
 * 
 * @author iu_oi
 * @since 0.0.2
 */
@Service
public class UserService {

    @Autowired /* jwt配置文件 */
    private JwtProperties jwtProperties;

    /**
     * 创建用户签名
     * 
     * @return 用户的签名
     */
    public String getSignature(int userId) {
        return JwtUtils.generateJws(
                jwtProperties.getKey(),
                jwtProperties.getExpiration(),
                userId);
    }

    /**
     * 验证用户签名
     * 
     * @param jws
     * @param userId
     * @throws ServiceError
     */
    public void checkSignature(String jws, int userId) throws ServiceError {
        try {
            int jwtId = JwtUtils.parseJws(jwtProperties.getKey(), jws);
            if (jwtId != userId)
                throw new ServiceError(ServiceErrorType.USER_AUTH_FAILED);
        } catch (ExpiredJwtException expiredJwtException) {
            throw new ServiceError(ServiceErrorType.USER_TOKEN_EXPIRED);
        } catch (JwtException jwtException) {
            throw new ServiceError(ServiceErrorType.USER_AUTH_FAILED);
        }
    }

    @Autowired /* user配置文件 */
    private UserProperties userProperties;

    /**
     * 验证用户是否符合user.xxx-format的规范
     * 
     * @param user
     * @throws ServiceError
     */
    public void check(User user) throws ServiceError {
        /* 验证用户名 */
        String username = user.getUsername();
        if (!username.matches(userProperties.getUsernameFormat()))
            throw new ServiceError(ServiceErrorType.BAD_USERNAME);
        /* 验证密码 */
        String password = user.getPassword();
        if (!password.matches(userProperties.getPasswordFormat()))
            throw new ServiceError(ServiceErrorType.BAD_PASSWORD);
    }

    @Autowired /* user数据库映射 */
    private UserMapper userMapper;

    /**
     * 用户登录
     * 
     * @param user
     * @return 有效用户
     * @throws ServiceError
     */
    public User login(User user) throws ServiceError {
        check(user);
        User databaseUser = userMapper.selectOne(
                new QueryWrapper<User>().eq("username", user.getUsername()));
        if (databaseUser == null)
            throw new ServiceError(ServiceErrorType.USER_NOT_FOUND);
        if (!databaseUser.getPassword().equals(user.getPassword()))
            throw new ServiceError(ServiceErrorType.USER_AUTH_FAILED);
        return databaseUser;

    }

    /**
     * 创建用户
     * 
     * @apiNote 用户id将被自动设置
     * @param user
     * @throws ServiceError
     */
    public void create(User user) throws ServiceError {
        check(user);
        User databaseUser = userMapper.selectOne(
                new QueryWrapper<User>().eq("username", user.getUsername()));
        if (databaseUser != null)
            throw new ServiceError(ServiceErrorType.USER_ALREADY_EXISTS);
        userMapper.insert(user);
    }

    /**
     * 更新用户
     * 
     * @param user
     * @throws ServiceError
     */
    public void update(User user) throws ServiceError {
        check(user);
        userMapper.updateById(user);
    }

    /**
     * 通过用户id查询用户
     * 
     * @param userId
     * @return 用户
     * @throws ServiceError
     */
    public User read(int userId) throws ServiceError {
        User databaseUser = userMapper.selectById(userId);
        if (databaseUser == null)
            throw new ServiceError(ServiceErrorType.USER_NOT_FOUND);
        return databaseUser;
    }

    /**
     * 通过用户名查询用户
     * 
     * @param username
     * @return 用户
     * @throws ServiceError
     */
    public User read(String username) {
        User databaseUser = userMapper.selectOne(
                new QueryWrapper<User>().eq("username", username));
        if (databaseUser == null)
            throw new ServiceError(ServiceErrorType.USER_NOT_FOUND);
        return databaseUser;
    }
}
