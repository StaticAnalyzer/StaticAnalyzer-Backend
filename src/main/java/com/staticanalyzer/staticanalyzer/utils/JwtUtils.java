package com.staticanalyzer.staticanalyzer.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;

/**
 * Jwt工具类
 * <p>
 * 该类提供Jwt签名与解析
 * </p>
 * 
 * @author YangYu
 * @since 0.1
 */
public class JwtUtils {

    /**
     * 生成Jws
     * 
     * @param key        签名密钥
     * @param expiration 签名过期时间
     * @param userId     用户id
     * @return 用户签名
     */
    public static String generateJws(java.security.Key key, java.time.Duration expiration, int userId) {
        java.util.Date now = new java.util.Date();
        java.util.Date expireDate = new java.util.Date(now.getTime() + expiration.toMillis());
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key)
                .compact();
    }

    /**
     * 从Jws解析用户id
     * 
     * @param key 解密密钥
     * @param jws 签名
     * @return 用户id
     * @throws JwtException
     */
    public static int parseJws(java.security.Key key, String jws) throws JwtException {
        String subject = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws)
                .getBody()
                .getSubject();
        return Integer.parseInt(subject);
    }

}
