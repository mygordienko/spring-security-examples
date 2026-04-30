package com.mygordienko.spring.security.examples.simpleottlogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class SimpleOttLoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleOttLoginApplication.class, args);
	}

}
