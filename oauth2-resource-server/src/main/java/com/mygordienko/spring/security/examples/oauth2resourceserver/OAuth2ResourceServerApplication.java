package com.mygordienko.spring.security.examples.oauth2resourceserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringBootApplication
@EnableJdbcRepositories
public class OAuth2ResourceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OAuth2ResourceServerApplication.class, args);
    }
}
