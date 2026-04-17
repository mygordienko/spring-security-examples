package com.mygordienko.spring.security.examples.oauth2client.resource.di;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.resource-server")
public record ResourceServerProperties(
        String baseUrl
) {
}
