package com.mygordienko.spring.security.examples.oauth2server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@ConfigurationPropertiesScan
@SpringBootApplication
public class DemoCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoCloudApplication.class, args);
    }

}
