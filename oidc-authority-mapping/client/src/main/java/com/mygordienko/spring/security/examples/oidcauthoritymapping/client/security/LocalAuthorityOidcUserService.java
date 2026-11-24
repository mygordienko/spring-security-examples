package com.mygordienko.spring.security.examples.oidcauthoritymapping.client.security;

import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

/**
 * Replaces the authorities of the OIDC principal with the authorities of the matching local account.
 * <p>
 * The provider is treated as an identity source only: nothing the provider claims about permissions is
 * carried over. A subject without a local account is rejected, otherwise it would pass
 * {@code anyRequest().authenticated()} and become a valid, unprivileged (but logged in) user.
 */
@Component
public class LocalAuthorityOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OAuth2UserService<OidcUserRequest, OidcUser> delegate;

    private final LocalUserAuthorities localUserAuthorities;

    public LocalAuthorityOidcUserService(
            LocalUserAuthorities localUserAuthorities,
            @Qualifier("oidcUserServiceDelegate") OAuth2UserService<OidcUserRequest, OidcUser> delegate) {
        this.localUserAuthorities = localUserAuthorities;
        this.delegate = delegate;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = this.delegate.loadUser(userRequest);
        String subject = oidcUser.getSubject();

        Set<GrantedAuthority> authorities = this.localUserAuthorities.findBySubject(subject)
            .orElseThrow(() -> new OAuth2AuthenticationException(new OAuth2Error(
                "access_denied",
                "OIDC subject '%s' has no local account".formatted(subject),
                null)));

        return new DefaultOidcUser(
            authorities,
            oidcUser.getIdToken(),
            oidcUser.getUserInfo(),
            IdTokenClaimNames.SUB);
    }
}
