package com.mygordienko.spring.security.examples.oidcauthoritymapping.provider.di;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Login/password pairs of the identity provider. There are intentionally no roles here:
 * this provider only confirms who the user is, it does not describe what the user may do.
 */
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
    }
}
