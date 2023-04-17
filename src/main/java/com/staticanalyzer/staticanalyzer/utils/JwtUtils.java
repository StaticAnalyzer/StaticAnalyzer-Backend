package com.staticanalyzer.staticanalyzer.utils;

import java.util.Date;

import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtils {
    private final Key secretKey;
    private final int expiration;

    public JwtUtils(
            @Value("${jwt.key}") String secret,
            @Value("${jwt.expiration}") int expiration) {
        String algorithmName = SignatureAlgorithm.HS256.getJcaName();
        secretKey = new SecretKeySpec(secret.getBytes(), algorithmName);
        this.expiration = expiration;
    }

    public String generateJws(int uid) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .setSubject(String.valueOf(uid))
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(secretKey)
                .compact();
    }

    public int parseJws(String jws) throws JwtException {
        String subject = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jws)
                .getBody()
                .getSubject();
        return Integer.parseInt(subject);
    }
}
