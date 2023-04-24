package com.staticanalyzer.staticanalyzer.utils;

import java.util.Date;

import java.security.Key;
import java.time.Duration;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public class JwtUtils {

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
