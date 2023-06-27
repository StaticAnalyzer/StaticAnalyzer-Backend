package com.staticanalyzer.staticanalyzer.config.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@lombok.Setter
@lombok.Getter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private java.security.Key key;

    private java.time.Duration expiration;

}
