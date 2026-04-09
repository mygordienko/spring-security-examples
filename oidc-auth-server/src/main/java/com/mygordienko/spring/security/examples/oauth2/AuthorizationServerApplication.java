package com.mygordienko.spring.security.examples.oauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class AuthorizationServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(AuthorizationServerApplication.class, args);
	}
}