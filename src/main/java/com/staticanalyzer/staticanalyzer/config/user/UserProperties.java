package com.staticanalyzer.staticanalyzer.config.user;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@lombok.Setter
@lombok.Getter
@Configuration
@ConfigurationProperties(prefix = "user")
public class UserProperties {

    private String passwordFormat;

    private String usernameFormat;

}
