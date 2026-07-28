package com.mygordienko.spring.security.examples.mfaspecificendpoint;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.mygordienko.spring.security.examples.mfaspecificendpoint.AuthConfigurationProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.authorization.AllRequiredFactorsAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagerFactories;
import org.springframework.security.authorization.RequiredFactor;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authorization.EnableMultiFactorAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.ott.DefaultGenerateOneTimeTokenRequestResolver;
import org.springframework.security.web.authentication.ott.GenerateOneTimeTokenRequestResolver;
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler;
import org.springframework.security.web.authentication.ott.RedirectOneTimeTokenGenerationSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMultiFactorAuthentication(authorities = {
    // NOTE: do not enforce MFA for all parts
    //  FactorGrantedAuthority.PASSWORD_AUTHORITY,
    //  FactorGrantedAuthority.OTT_AUTHORITY
})
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled=true)
public class SecurityConfiguration {
  @Bean
  public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder, AuthConfigurationProperties authProperties) {
    return new InMemoryUserDetailsManager(
      authProperties.getUsers().stream()
      .map(user -> composeUser(user.getEmail(), passwordEncoder.encode(user.getPassword()), user.getRoles()))
      .toList()
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

    // NOTE: this is a factory, use it to build complex conditions, like the one below
    var mfaFactory = AuthorizationManagerFactories.multiFactor()
            .requireFactors(FactorGrantedAuthority.PASSWORD_AUTHORITY, FactorGrantedAuthority.OTT_AUTHORITY)
            .build();

    // NOTE: this is a manager, it contains MFA conditions as well as authority check
    //  so we do not need to check for hasRole() later
    var mfaManager = AllRequiredFactorsAuthorizationManager.builder()
            .requireFactor(RequiredFactor.builder()
                    .passwordAuthority()
                    .authority("ROLE_ADMIN")
                    .build())
            .requireFactor(RequiredFactor.builder()
                    .ottAuthority()
                    // NOTE: authentication MUST not be older than 1 minute
                    .validDuration(Duration.ofMinutes(1))
                    .build())
            .build();

    http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/css/**", "/js/**", "/images/**", "/svg/**", "/favicon.ico").permitAll()
        // NOTE: this means MFA + admin role is required to access
        //  action execution is additionally guarded by checking session lifetime
        .requestMatchers(HttpMethod.GET, "/adminpanel").access(mfaFactory.hasRole("ADMIN"))
        .requestMatchers(HttpMethod.POST, "/adminpanel").access(mfaManager)

        .requestMatchers("/login").permitAll()
        .requestMatchers("/error").permitAll()
        .anyRequest().authenticated()
    )
    .formLogin(login -> login
      .defaultSuccessUrl("/"))
    .logout(Customizer.withDefaults());
    
    return http.build();
  }

  private static final String OTT_GENERATE_PATH = "/ott/custom/generate";
  private static final String OTT_LOGIN_PATH = "/ott/custom/login";

  /**
   * Use Customizer to "add" new login option without disrupting existing security chain.
   */
  @Bean
  public Customizer<HttpSecurity> httpSecurityCustomizer() {
    return http -> http
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/ott/sent").permitAll()
      )
      .oneTimeTokenLogin(ott -> ott
        .tokenGeneratingUrl(OTT_GENERATE_PATH)
        .loginProcessingUrl(OTT_LOGIN_PATH)   // POST, csrf is enabled by default
        .defaultSubmitPageUrl(OTT_LOGIN_PATH) // GET
        .tokenGenerationSuccessHandler(new CustomTokenGenerationSuccessHandler())
        .generateRequestResolver(new CustomTtlTokenRequestResolver())
      );
  }

  public static class CustomTokenGenerationSuccessHandler implements OneTimeTokenGenerationSuccessHandler {

    private final OneTimeTokenGenerationSuccessHandler redirectHandler = new RedirectOneTimeTokenGenerationSuccessHandler("/ott/sent");

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, OneTimeToken oneTimeToken)
        throws IOException, ServletException {
          UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(UrlUtils.buildFullRequestUrl(request))
            .replacePath(request.getContextPath())
            .replaceQuery(null)
            .fragment(null)
            .path(OTT_LOGIN_PATH)
            .queryParam("token", oneTimeToken.getTokenValue());

          String loginLink = builder.toUriString();
          String email = oneTimeToken.getUsername();

          IO.println("Here is your login link " + loginLink);
          redirectHandler.handle(request, response, oneTimeToken);
    }
  }

  /**
   * Setting custom TTL for our token.
   */
  public static class CustomTtlTokenRequestResolver implements GenerateOneTimeTokenRequestResolver {

    private GenerateOneTimeTokenRequestResolver delegate = new DefaultGenerateOneTimeTokenRequestResolver();
    @Override
    public GenerateOneTimeTokenRequest resolve(HttpServletRequest request) {
      GenerateOneTimeTokenRequest originTokenRequest = delegate.resolve(request);
      return new GenerateOneTimeTokenRequest(originTokenRequest.getUsername(), Duration.ofMinutes(2));
    }
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
