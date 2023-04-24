package com.staticanalyzer.staticanalyzer.config.jwt;

import lombok.Getter;
import lombok.Setter;

import java.security.Key;
import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private Key key;

    private Duration expiration;
}
