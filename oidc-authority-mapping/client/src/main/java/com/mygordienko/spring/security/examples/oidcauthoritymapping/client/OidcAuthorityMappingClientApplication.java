package com.mygordienko.spring.security.examples.oidcauthoritymapping.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class OidcAuthorityMappingClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(OidcAuthorityMappingClientApplication.class, args);
	}
}
