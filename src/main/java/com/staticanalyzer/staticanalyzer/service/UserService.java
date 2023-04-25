package com.staticanalyzer.staticanalyzer.service;

import io.jsonwebtoken.JwtException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.staticanalyzer.staticanalyzer.config.jwt.JwtProperties;
import com.staticanalyzer.staticanalyzer.config.user.UserProperties;
import com.staticanalyzer.staticanalyzer.entity.user.User;
import com.staticanalyzer.staticanalyzer.mapper.UserMapper;
import com.staticanalyzer.staticanalyzer.utils.JwtUtils;

/**
 * 用户服务
 * 增删改查以及验证等
 * 
 * @author iu_oi
 * @verion 0.0.1
 */
@Service
public class UserService {

    /**
     * jwt配置文件
     */
    @Autowired
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
     * @return 如果验证成功返回{@code 真}，否则为{@code 假}
     */
    public boolean verifySignature(String jws, int userId) {
        try {
            int jwtId = JwtUtils.parseJws(jwtProperties.getKey(), jws);
            if (jwtId == userId)
                return true;
            return false;
        } catch (JwtException jwtException) {
            /* todo: 处理不同类型的异常并转化成用户异常 */
            return false;
        }
    }

    /**
     * user配置文件
     */
    @Autowired
    private UserProperties userProperties;

    /**
     * 验证用户是否符合user.xxx-format的规范
     * 
     * @param user
     * @return 验证成功返回{@code 真}，否则返回{@code 假}
     */
    public boolean check(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        if (!username.matches(userProperties.getUsernameFormat())) {
            /* todo: 扔出用户异常 */
            return false;
        }
        if (!password.matches(userProperties.getPasswordFormat())) {
            /* todo: 扔出用户异常 */
            return false;
        }
        return true;
    }

    /**
     * user数据库映射
     */
    @Autowired
    private UserMapper userMapper;

    /**
     * 创建用户
     * 
     * @apiNote user无需填写id字段，它将被自动设置
     * @param user
     */
    public void create(User user) {
        userMapper.insert(user);
    }

    /**
     * 更新用户
     * 
     * @param user
     */
    public void update(User user) {
        userMapper.updateById(user);
    }

    /**
     * 通过用户id查询用户
     * 
     * @param userId
     * @return 查询到的用户，查询失败返回{@code null}
     */
    public User read(int userId) {
        return userMapper.selectById(userId);
    }

    /**
     * 通过用户名查询用户
     * 
     * @param username
     * @return 查询到的用户，查询失败返回{@code null}
     */
    public User read(String username) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
    }
}
