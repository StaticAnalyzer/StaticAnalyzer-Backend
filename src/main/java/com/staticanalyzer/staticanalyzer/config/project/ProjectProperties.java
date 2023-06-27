package com.staticanalyzer.staticanalyzer.config.project;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@lombok.Setter
@lombok.Getter
@Configuration
@ConfigurationProperties(prefix = "project")
public class ProjectProperties {

    private java.time.Duration expiration;

    private int taskLimit;

}
