package com.mygordienko.spring.security.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class OAuth2SimpleClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(OAuth2SimpleClientApplication.class, args);
    }
}
