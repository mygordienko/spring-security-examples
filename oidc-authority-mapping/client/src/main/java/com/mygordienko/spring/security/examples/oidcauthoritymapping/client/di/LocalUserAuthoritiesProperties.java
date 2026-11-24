package com.mygordienko.spring.security.examples.oidcauthoritymapping.client.di;

import java.util.List;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Local account table of the client application, configured as the {@code app.local-users} list. Accounts are
 * keyed by OIDC subject, and the authorities are the only authorities the client will ever use for
 * authorization - whatever the provider says about the user is ignored.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class LocalUserAuthoritiesProperties {

    @NestedConfigurationProperty
    private List<LocalUser> localUsers = List.of();

    @Getter
    @Setter
    public static class LocalUser {
        private String subject;
        private Set<String> authorities = Set.of();
    }
}
