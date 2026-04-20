package com.mygordienko.spring.security.examples.oauth2client.security.di;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/error", "/css/**", "/oauth2/**", "/login/oauth2/**").permitAll()
                        .requestMatchers("/interviews", "/api/v1/interviews").hasAuthority("ROLE_HR")
                        .requestMatchers("/demands", "/api/v1/demands").hasAuthority("ROLE_MANAGER")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService()))
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        OidcUserService delegate = new OidcUserService();

        return userRequest -> {
            OidcUser oidcUser = delegate.loadUser(userRequest);
            Set<GrantedAuthority> authorities = new HashSet<>(oidcUser.getAuthorities());
            authorities.addAll(mapAuthorities(oidcUser.getClaimAsStringList("authorities")));

            OidcIdToken idToken = oidcUser.getIdToken();
            return new DefaultOidcUser(authorities, idToken, oidcUser.getUserInfo(), "sub");
        };
    }

    private Collection<? extends GrantedAuthority> mapAuthorities(List<String> claims) {
        if (claims == null) {
            return List.of();
        }

        return claims.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Bean
    public OAuth2AuthorizedClientManager oauth2AuthorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository
    ) {
        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .authorizationCode()
                .refreshToken()
                .build();

        DefaultOAuth2AuthorizedClientManager manager =
                new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
        manager.setAuthorizedClientProvider(authorizedClientProvider);
        // NOTE: Spring does refresh access token automatically when it expires (using refresh token).
        //  Spring DOES NOT automatically invalidate user's session when refresh token expires.
        //  Refresh token does not have (and does not have to) expiration timestamp, so there is no way for
        //  Spring to proactively send user to re-login. Spring has to try to refresh access token and catch an exception
        //  Here we use error handler to lookup current request and invalidate session explicitly.
        manager.setAuthorizationFailureHandler((authorizationException, principal, attributes) -> {
            Optional.ofNullable(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()))
                    .map(servletRequestAttributes -> servletRequestAttributes.getRequest())
                    .map(httpServletRequest -> httpServletRequest.getSession())
                    .ifPresent(httpSession -> {
                        System.out.println("Invalidating session: " + httpSession.getId());
                        httpSession.invalidate();
                    });
        });
        return manager;
    }
}
