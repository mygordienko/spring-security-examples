package com.mygordienko.spring.security.examples.oidcauthoritymapping.client.di;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

import com.mygordienko.spring.security.examples.oidcauthoritymapping.client.security.LocalAuthorityOidcUserService;

@Configuration
@EnableWebSecurity
public class ClientSecurityConfiguration {

    /**
     * Standard OIDC user loader. It is kept as a separate bean so the local-authority mapping service can be
     * tested without making a real user-info request to the provider.
     */
    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserServiceDelegate() {
        return new OidcUserService();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ClientRegistrationRepository clientRegistrationRepository,
            LocalAuthorityOidcUserService localAuthorityOidcUserService
    ) throws Exception {

        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/login", "/access-denied", "/error", "/favicon.ico",
                        "/oauth2/**", "/login/oauth2/**").permitAll()
                .requestMatchers("/user").hasAuthority("ROLE_USER")
                .requestMatchers("/restricted").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .authorizationEndpoint(endpoint -> endpoint
                    .authorizationRequestResolver(pkceAuthorizationRequestResolver(clientRegistrationRepository)))
                .userInfoEndpoint(userInfo -> userInfo
                    .oidcUserService(localAuthorityOidcUserService))
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/access-denied")
            );

        return http.build();
    }

    /**
     * This example's provider requires a proof key, so the authorization code request has to carry a
     * PKCE challenge.
     */
    private static OAuth2AuthorizationRequestResolver pkceAuthorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {
        DefaultOAuth2AuthorizationRequestResolver resolver =
            new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository);
        resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
        return resolver;
    }
}
