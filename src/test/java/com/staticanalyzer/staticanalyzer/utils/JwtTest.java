package com.staticanalyzer.staticanalyzer.utils;

import static org.junit.Assert.assertEquals;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.SignatureAlgorithm;

// 本测试与Spring Boot无关
public class JwtTest {

    @org.junit.Test
    public void testJwt() {
        java.util.Random random = new java.util.Random();
        byte[] keyBytes = new byte[256];

        // 随机化测试
        random.nextBytes(keyBytes);
        java.security.Key key = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
        java.time.Duration expiration = java.time.Duration.parse(String.format("PT%dS", random.nextInt(600)));

        for (int i = 0; i < 100; i++) {
            int id = random.nextInt();
            String jws = JwtUtils.generateJws(key, expiration, id);
            assertEquals(JwtUtils.parseJws(key, jws), id);
        }
    }

}
