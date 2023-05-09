package com.staticanalyzer.staticanalyzer.utils;

import java.security.Key;
import java.time.Duration;
import java.util.Random;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import com.staticanalyzer.staticanalyzer.config.jwt.JwtProperties;

/**
 * jwt测试类
 * 
 * @author iu_oi
 * @version 0.0.1
 */
public class JwtTest {

    @Autowired /* jwt配置文件 */
    private JwtProperties jwtProperties;

    /**
     * 生成jws并验证之
     */
    @Test
    public void testJwt() {
        Random random = new Random(0xdeadbeef);
        Key key = jwtProperties.getKey();
        Duration expiration = jwtProperties.getExpiration();
        for (int i = 0; i < 100; i++) {
            int id = random.nextInt();
            String jws = JwtUtils.generateJws(key, expiration, id);
            assertEquals(JwtUtils.parseJws(key, jws), id);
        }
    }
}
