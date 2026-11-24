package com.mygordienko.spring.security.examples.oidcauthoritymapping.client.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.mygordienko.spring.security.examples.oidcauthoritymapping.client.di.LocalUserAuthoritiesProperties;
import com.mygordienko.spring.security.examples.oidcauthoritymapping.client.di.LocalUserAuthoritiesProperties.LocalUser;

/**
 * Local subject -&gt; authorities table. This is the only source of authorities in the client application.
 */
@Component
public class LocalUserAuthorities {

    private final Map<String, Set<GrantedAuthority>> authoritiesBySubject;

    public LocalUserAuthorities(LocalUserAuthoritiesProperties properties) {
        Assert.state(!properties.getLocalUsers().isEmpty(),
            "app.local-users must define at least one local account");

        Map<String, Set<GrantedAuthority>> bySubject = new HashMap<>();
        for (LocalUser user : properties.getLocalUsers()) {
            String subject = Objects.requireNonNull(user.getSubject(), "app.local-users[].subject must be set");
            Assert.state(!bySubject.containsKey(subject),
                () -> "Duplicate local account for OIDC subject '%s'".formatted(subject));
            Assert.state(user.getAuthorities() != null && !user.getAuthorities().isEmpty(),
                () -> "Local account for OIDC subject '%s' must define at least one authority".formatted(subject));
            bySubject.put(subject, composeAuthorities(user.getAuthorities()));
        }
        this.authoritiesBySubject = Map.copyOf(bySubject);
    }

    /**
     * @return authorities of the local account bound to the given OIDC subject, or empty when the subject
     * has no local account
     */
    public Optional<Set<GrantedAuthority>> findBySubject(String subject) {
        return Optional.ofNullable(this.authoritiesBySubject.get(subject));
    }

    private static Set<GrantedAuthority> composeAuthorities(Set<String> authorities) {
        Set<GrantedAuthority> result = new HashSet<>();
        for (String authority : authorities) {
            result.add(new SimpleGrantedAuthority(authority));
        }
        return Set.copyOf(result);
    }
}
