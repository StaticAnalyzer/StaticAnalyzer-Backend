package com.staticanalyzer.staticanalyzer.config.jwt;

import lombok.Getter;
import lombok.Setter;

import java.security.Key;
import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * jwt配置类
 * 
 * @author iu_oi
 * @since 0.0.2
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /* 签名使用的H256密钥 */
    private Key key;

    /* jwt过期时间 */
    private Duration expiration;
}
