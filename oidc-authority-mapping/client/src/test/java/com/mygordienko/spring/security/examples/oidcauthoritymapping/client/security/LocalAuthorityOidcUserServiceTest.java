package com.mygordienko.spring.security.examples.oidcauthoritymapping.client.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.mygordienko.spring.security.examples.oidcauthoritymapping.client.di.LocalUserAuthoritiesProperties;
import com.mygordienko.spring.security.examples.oidcauthoritymapping.client.di.LocalUserAuthoritiesProperties.LocalUser;

class LocalAuthorityOidcUserServiceTest {

    private static final OAuth2UserService<OidcUserRequest, OidcUser> PROVIDER_USER_SERVICE = request ->
        new DefaultOidcUser(
            List.of(
                new SimpleGrantedAuthority("SCOPE_openid"),
                new SimpleGrantedAuthority("PROVIDER_ADMIN")),
            request.getIdToken());

    private final LocalAuthorityOidcUserService service = new LocalAuthorityOidcUserService(
        localUserAuthorities(),
        PROVIDER_USER_SERVICE);

    @Test
    void replacesProviderAuthoritiesWithLocalAuthorities() {
        OidcUser result = this.service.loadUser(requestFor("userA"));

        assertThat(result.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly("ROLE_USER");
        assertThat(result.getIdToken().getSubject()).isEqualTo("userA");
    }

    @Test
    void rejectsOidcSubjectWithoutLocalAccount() {
        OidcUserRequest request = requestFor("userC");

        assertThatThrownBy(() -> this.service.loadUser(request))
            .isInstanceOfSatisfying(OAuth2AuthenticationException.class, exception ->
                assertThat(exception.getError().getErrorCode()).isEqualTo("access_denied"));
    }

    private static OidcUserRequest requestFor(String subject) {
        ClientRegistration registration = ClientRegistration.withRegistrationId("authority-mapping-client")
            .clientId("authority-mapping-client")
            .clientSecret("secret")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("http://127.0.0.1:8082/login/oauth2/code/authority-mapping-client")
            .authorizationUri("http://auth.local:9100/oauth2/authorize")
            .tokenUri("http://auth.local:9100/oauth2/token")
            .userInfoUri("http://auth.local:9100/userinfo")
            .userNameAttributeName(IdTokenClaimNames.SUB)
            .build();

        Instant now = Instant.now();
        OidcIdToken idToken = new OidcIdToken(
            "id-token",
            now,
            now.plusSeconds(60),
            Map.of(IdTokenClaimNames.SUB, subject));
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            "access-token",
            now,
            now.plusSeconds(60));

        return new OidcUserRequest(registration, accessToken, idToken);
    }

    private static LocalUserAuthorities localUserAuthorities() {
        LocalUserAuthoritiesProperties properties = new LocalUserAuthoritiesProperties();
        properties.setLocalUsers(List.of(
            localUser("userA", Set.of("ROLE_USER")),
            localUser("userB", Set.of("ROLE_USER", "ROLE_ADMIN"))
        ));
        return new LocalUserAuthorities(properties);
    }

    private static LocalUser localUser(String subject, Set<String> authorities) {
        LocalUser localUser = new LocalUser();
        localUser.setSubject(subject);
        localUser.setAuthorities(authorities);
        return localUser;
    }
}
