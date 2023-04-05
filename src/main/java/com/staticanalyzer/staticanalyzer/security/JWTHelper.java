package com.staticanalyzer.staticanalyzer.security;

import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;

@Component
public class JWTHelper {
    private final long expiration;
    private final SignatureAlgorithm algorithm;
    private final Key secretKey;

    public JWTHelper(@Value("${jwt.key}") String secret, @Value("${jwt.expiration}") String expiration) {
        this.expiration = Integer.parseInt(expiration);
        algorithm = SignatureAlgorithm.HS256;
        secretKey = new SecretKeySpec(secret.getBytes(), algorithm.getJcaName());
    }

    public String generate(int userId) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration);
        JwtBuilder builder = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(secretKey);
        return builder.compact();
    }

    public Claims parse(String jwt) {
        try {
            JwtParser parser = Jwts.parserBuilder().setSigningKey(secretKey).build();
            return parser.parseClaimsJws(jwt).getBody();
        } catch (JwtException je) {
            return null;
        }
    }
}
