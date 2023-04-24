package com.staticanalyzer.staticanalyzer.config.user;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "user")
public class UserProperties {

    private String passwordFormat;

    private String usernameFormat;
}
