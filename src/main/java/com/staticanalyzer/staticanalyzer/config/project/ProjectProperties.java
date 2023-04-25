package com.staticanalyzer.staticanalyzer.config.project;

import java.time.Duration;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * project配置类
 * 
 * @author iu_oi
 * @since 0.0.2
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "project")
public class ProjectProperties {

    /* project缓存过期时间 */
    private Duration expiration;

    /* project线程池限制 */
    private int taskLimit;
}
