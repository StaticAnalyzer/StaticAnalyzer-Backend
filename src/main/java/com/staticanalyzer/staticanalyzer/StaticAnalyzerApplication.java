package com.staticanalyzer.staticanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/************************
 * 我们的Static Analyzer *
 ************************/

@SpringBootApplication
@EnableSwagger2
public class StaticAnalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(StaticAnalyzerApplication.class, args);
	}

}
