package com.mygordienko.spring.security.examples.simpleform;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.web.servlet.ServletContextInitializer;
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

import jakarta.servlet.SessionTrackingMode;

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
        .requestMatchers("/css/**", "/js/**", "/images/**", "/svg/**", "/favicon.ico").permitAll()
        .requestMatchers("/login").permitAll()
        .requestMatchers("/error").permitAll()
        .anyRequest().authenticated()
    )
    .formLogin(login -> login
      .defaultSuccessUrl("/"))
    .logout(Customizer.withDefaults());
    
    return http.build();
  }

  /**
   * Instructs Tomcat to not attempt to add session to the redirect urls when cookie is missing.
   * Without this, there will be error in the logs (which does not affect login)
   *
   *  ```
   *  Redirecting to /login;jsessionid=A733B616CD14F90DDE4AF7BA5DB9E965
   *   ...
   *  Rejecting request due to: The request was rejected because the URL contained a potentially malicious String ";"
   *  ```
   * 
   * alternative: Set property: server.servlet.session.tracking-modes=cookie
   * 
   * @return
   */
  @Bean
  public ServletContextInitializer servletContextInitializer() {
    return servletContext ->
        servletContext.setSessionTrackingModes(
            Set.of(SessionTrackingMode.COOKIE)
        );
  }

}
