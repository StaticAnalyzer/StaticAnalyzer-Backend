package com.staticanalyzer.staticanalyzer.config;

import com.google.common.base.Predicates;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import com.staticanalyzer.staticanalyzer.annotation.swagger.ExcludeSwagger;

@Configuration
public class SwaggerConfigurer {

    @Value("${spring.swagger.enabled}")
    private Boolean enabled;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.version}")
    private String applicationVersion;

    @Value("${spring.application.description}")
    private String applicationDescription;

    @Value("${spring.swagger.contact.name}")
    private String contactName;

    @Value("${spring.swagger.contact.url}")
    private String contactUrl;

    @Value("${spring.swagger.contact.email}")
    private String contactEmail;

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title(applicationName)
                .description(applicationDescription)
                .version(applicationVersion)
                .contact(new Contact(contactName, contactUrl, contactEmail))
                .build();
    }

    @Bean
    public Docket createDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
                .enable(enabled)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.staticanalyzer.staticanalyzer"))
                .apis(Predicates.not(RequestHandlerSelectors.withMethodAnnotation(ExcludeSwagger.class)))
                .apis(Predicates.not(RequestHandlerSelectors.withClassAnnotation(ExcludeSwagger.class)))
                .paths(PathSelectors.any())
                .build();
    }

}
