package com.mygordienko.spring.security.examples.jwtauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled=true)
public class SecurityConfiguration {
  @Bean
  public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    return new InMemoryUserDetailsManager(
      List.of(
          composeUser("john@mail.com", passwordEncoder.encode("pass"), List.of("admin", "user")),
          composeUser("eve@mail.com", passwordEncoder.encode("pass"), List.of("user"))
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
  public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
        .requestMatchers("/auth/**").permitAll()
        .requestMatchers("/api-docs/**").permitAll()
        .requestMatchers("/api-ui/**").permitAll()
        .anyRequest().authenticated())
    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    // NOTE: since we used `authorizeHttpRequests()` we put our filter before AuthorizationFilter
    .addFilterBefore(jwtAuthFilter, AuthorizationFilter.class);
    
    return http.build();
  }

  /**
   * NOTE: Spring does not expose default AuthenticationManager bean (ProviderManager). That is done intentionally.
   *  If we want to use custom authentication logic that involves calling Spring's defaults, we have to explicitly
   *  define the bean.
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
    return config.getAuthenticationManager();
  }
}
