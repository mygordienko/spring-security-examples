package com.mygordienko.spring.security.example.simpleldap;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.SessionTrackingMode;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {
  @Bean
  public SecurityFilterChain defauFilterChain(HttpSecurity http) {
    http.authorizeHttpRequests(authz -> authz
      .requestMatchers("/login", "/js/**", "/css/**", "/favicon.ico", "/images/**").permitAll()
      .requestMatchers("/error").permitAll()
      .anyRequest().authenticated()
    )
    .httpBasic(basic -> basic.disable())
    .formLogin(form -> form.defaultSuccessUrl("/"))
    .logout(Customizer.withDefaults())
    ;

    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    //NOTE:  Defining even an empty manager stops default password generation
    return new InMemoryUserDetailsManager();
  }

  @Bean
  public ServletContextInitializer servletContextInitializer() {
    return servletContext ->
        servletContext.setSessionTrackingModes(
            Set.of(SessionTrackingMode.COOKIE)
        );
  }

  @Autowired
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
    .ldapAuthentication()
    .userDnPatterns("uid={0},ou=Users")
    .contextSource()
    .url("ldap://127.0.0.1:10389/dc=ldap,dc=example")
    .managerDn("uid=admin,ou=system")
    .managerPassword("secret");
  }

  /**
   * NOTE: we do not need PasswordEncoder bean because credentials validation is delegated to LDAP server.
   * Our application just passes provided credentials as-is. 
   */
  // @Bean
  // public PasswordEncoder passwordEncoder() {
  //   return NoOpPasswordEncoder.getInstance();
  // }
}
