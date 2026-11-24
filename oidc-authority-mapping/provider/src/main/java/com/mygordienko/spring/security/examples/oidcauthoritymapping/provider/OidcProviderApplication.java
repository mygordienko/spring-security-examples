package com.mygordienko.spring.security.examples.oidcauthoritymapping.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class OidcProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(OidcProviderApplication.class, args);
	}
}
