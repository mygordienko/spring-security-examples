package com.mygordienko.spring.security.examples.simpleform;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled=true)
public class SecurityConfiguration {
  @Bean
  public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    return new InMemoryUserDetailsManager(
      List.of(
          composeUser("john", passwordEncoder.encode("pass"), List.of("admin")),
          composeUser("eve", passwordEncoder.encode("pass"), List.of("user"))
      )
    );
  }

  private static UserDetails composeUser(String login, String password, Collection<String> authorities) {
    return new User(
        login,
        password,
        true,
        true,
        true,
        true,
        authorities.stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toSet())
    );
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) {
    http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/css/**", "/js/**", "/images/**", "/svg/**", "/webjars/**", "/favicon.ico").permitAll()
        .requestMatchers("/login").permitAll()
        .anyRequest().authenticated()
    )
    .formLogin(login -> login
      .defaultSuccessUrl("/", true))
    .logout(Customizer.withDefaults());
    
    return http.build();
  }
}
