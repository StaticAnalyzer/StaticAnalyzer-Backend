package com.staticanalyzer.staticanalyzer.utils;

import static org.junit.Assert.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;

import com.staticanalyzer.staticanalyzer.config.jwt.JwtProperties;

public class JwtTest {

    @Autowired
    private JwtProperties jwtProperties;

    @org.junit.Test
    public void testJwt() {
        java.util.Random random = new java.util.Random(0xdeadbeef);
        java.security.Key key = jwtProperties.getKey();
        java.time.Duration expiration = jwtProperties.getExpiration();
        for (int i = 0; i < 100; i++) {
            int id = random.nextInt();
            String jws = JwtUtils.generateJws(key, expiration, id);
            assertEquals(JwtUtils.parseJws(key, jws), id);
        }
    }

}
