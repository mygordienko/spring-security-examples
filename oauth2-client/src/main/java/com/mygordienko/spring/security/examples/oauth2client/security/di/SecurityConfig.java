package com.mygordienko.spring.security.examples.oauth2client.security.di;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

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
}
