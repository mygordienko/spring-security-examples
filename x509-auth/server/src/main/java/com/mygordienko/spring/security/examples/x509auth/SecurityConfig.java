package com.mygordienko.spring.security.examples.x509auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.x509.SubjectX500PrincipalExtractor;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                // NOTE; Default extractor returns CN value
                .x509(x509 -> x509
                        .userDetailsService(userDetailsService())
                        .x509PrincipalExtractor(new SubjectX500PrincipalExtractor())

                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        ;

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // The CN from the client cert must match a username here to grant roles
        UserDetails clientApp = User.withUsername("client-a")
                .password("") // No password needed for TLS auth
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(clientApp);
    }
}