package com.staticanalyzer.staticanalyzer.utils;

import static org.junit.Assert.assertEquals;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.SignatureAlgorithm;

// 本测试与Spring Boot无关
public class JwtTest {
    
    private static java.util.Random JWT_RANDOM = new java.util.Random();

    @org.junit.Test
    public void testJwt() {
        byte[] keyBytes = new byte[256];

        // 随机化测试
        JWT_RANDOM.nextBytes(keyBytes);
        java.security.Key key = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
        java.time.Duration expiration = java.time.Duration.parse(String.format("PT%dS", JWT_RANDOM.nextInt(600)));

        for (int i = 0; i < 100; i++) {
            int id = JWT_RANDOM.nextInt(32768);
            String jws = JwtUtils.generateJws(key, expiration, id);
            assertEquals(JwtUtils.parseJws(key, jws), id);
            System.out.println(String.format("JWS verified with id = %d", id));
        }
        System.out.println("JWS test passed!");
    }

}
