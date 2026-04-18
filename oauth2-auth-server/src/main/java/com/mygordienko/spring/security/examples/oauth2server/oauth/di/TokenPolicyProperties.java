package com.mygordienko.spring.security.examples.oauth2server.oauth.di;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.token")
public record TokenPolicyProperties(
        Duration accessTtl,
        Duration refreshTtl
) {
}
