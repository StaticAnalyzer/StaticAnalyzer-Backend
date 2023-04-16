package com.staticanalyzer.staticanalyzer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicates;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class Swagger2Config {

    @Value("${spring.application.name}")
    private String applicationName;

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title(applicationName)
                .description("Static Analyzer Backend API")
                .version("0.0.1-SNAPSHOT")
                .contact(new Contact("YuXiang", "github.com/StaticAnalyzer", "201180033@smail.nju.edu.cn"))
                .build();
    }

    @Bean
    public Docket createDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.staticanalyzer.staticanalyzer.controller"))
                .apis(Predicates.not(RequestHandlerSelectors.withMethodAnnotation(ExcludeSwagger.class)))
                .apis(Predicates.not(RequestHandlerSelectors.withClassAnnotation(ExcludeSwagger.class)))
                .paths(PathSelectors.any())
                .build();
    }
}
