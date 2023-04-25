package com.staticanalyzer.staticanalyzer.config.user;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * user配置类
 * 
 * @author iu_oi
 * @since 0.0.2
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "user")
public class UserProperties {

    /* user密码正则 */
    private String passwordFormat;

    /* user名称正则 */
    private String usernameFormat;
}
