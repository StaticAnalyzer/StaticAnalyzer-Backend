package com.staticanalyzer.staticanalyzer.utils;

import java.util.Date;

import java.security.Key;
import java.time.Duration;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

/**
 * jwt工具类
 * 
 * @author iu_oi
 * @since 0.0.1
 */
public class JwtUtils {

    /**
     * 生成jws
     * 
     * @param key
     * @param expiration
     * @param userId
     * @return 用户签名
     */
    public static String generateJws(Key key, Duration expiration, int userId) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration.toMillis());
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key)
                .compact();
    }

    /**
     * 从jws解析用户id
     * 
     * @param key
     * @param jws
     * @return 用户id
     * @throws JwtException
     */
    public static int parseJws(Key key, String jws) throws JwtException {
        String subject = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws)
                .getBody()
                .getSubject();
        return Integer.parseInt(subject);
    }
}
