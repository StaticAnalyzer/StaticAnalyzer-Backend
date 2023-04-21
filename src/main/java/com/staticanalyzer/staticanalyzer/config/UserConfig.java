package com.staticanalyzer.staticanalyzer.config;

import java.util.regex.Pattern;

import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.SignatureAlgorithm;

import lombok.Getter;

@Getter
@Configuration
public class UserConfig {
    private Key key;
    private int expiration;
    private Pattern usernamePattern;
    private Pattern passwordPattern;

    public UserConfig(
            @Value("${user.jwt.secret}") String secret,
            @Value("${user.jwt.expiration}") int expiration,
            @Value("${user.auth.regex.username}") String usernameRegex,
            @Value("${user.auth.regex.password}") String passwordRegex) {
        key = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        this.expiration = expiration;
        usernamePattern = Pattern.compile(usernameRegex);
        passwordPattern = Pattern.compile(passwordRegex);
    }
}
