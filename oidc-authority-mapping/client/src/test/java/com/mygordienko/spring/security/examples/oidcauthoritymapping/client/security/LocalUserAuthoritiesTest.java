package com.mygordienko.spring.security.examples.oidcauthoritymapping.client.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import com.mygordienko.spring.security.examples.oidcauthoritymapping.client.di.LocalUserAuthoritiesProperties;
import com.mygordienko.spring.security.examples.oidcauthoritymapping.client.di.LocalUserAuthoritiesProperties.LocalUser;

class LocalUserAuthoritiesTest {

    private final LocalUserAuthorities localUserAuthorities = new LocalUserAuthorities(properties());

    @Test
    void resolvesBothAuthoritiesOfAdminSubject() {
        assertThat(localUserAuthorities.findBySubject("userB"))
            .hasValueSatisfying(authorities -> assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN"));
    }

    @Test
    void resolvesSingleAuthorityOfPlainSubject() {
        assertThat(localUserAuthorities.findBySubject("userA"))
            .hasValueSatisfying(authorities -> assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER"));
    }

    @Test
    void unknownSubjectIsNotMapped() {
        assertThat(localUserAuthorities.findBySubject("userC")).isEmpty();
    }

    @Test
    void emptyLocalAccountTableIsRejectedAtStartup() {
        assertThatThrownBy(() -> new LocalUserAuthorities(new LocalUserAuthoritiesProperties()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("app.local-users");
    }

    @Test
    void localAccountWithoutAuthoritiesIsRejectedAtStartup() {
        LocalUserAuthoritiesProperties properties = new LocalUserAuthoritiesProperties();
        properties.setLocalUsers(List.of(localUser("userA", Set.of())));

        assertThatThrownBy(() -> new LocalUserAuthorities(properties))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("at least one authority");
    }

    @Test
    void duplicateLocalAccountIsRejectedAtStartup() {
        LocalUserAuthoritiesProperties properties = new LocalUserAuthoritiesProperties();
        properties.setLocalUsers(List.of(
            localUser("userA", Set.of("ROLE_USER")),
            localUser("userA", Set.of("ROLE_ADMIN"))
        ));

        assertThatThrownBy(() -> new LocalUserAuthorities(properties))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Duplicate local account");
    }

    private static LocalUserAuthoritiesProperties properties() {
        LocalUserAuthoritiesProperties properties = new LocalUserAuthoritiesProperties();
        properties.setLocalUsers(List.of(
            localUser("userA", Set.of("ROLE_USER")),
            localUser("userB", Set.of("ROLE_USER", "ROLE_ADMIN"))
        ));
        return properties;
    }

    private static LocalUser localUser(String subject, Set<String> authorities) {
        LocalUser localUser = new LocalUser();
        localUser.setSubject(subject);
        localUser.setAuthorities(authorities);
        return localUser;
    }
}
