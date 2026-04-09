package com.mygordienko.spring.security.examples.oauth2.di;

import java.util.Collection;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "credentials")
public class UserCredentialsConfigurationProperties {

    @NestedConfigurationProperty
    private List<InnerUserCredentials> users = List.of();

    @Getter
    @Setter
    public static class InnerUserCredentials {
        private String login;
        private String password;
        private Collection<String> roles = List.of();
    }
}
