package com.staticanalyzer.staticanalyzer.utils;

import java.util.Date;

import java.security.Key;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public class JwtUtils {

    public static String generateJws(Key key, int expiration, int uid) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .setSubject(String.valueOf(uid))
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
