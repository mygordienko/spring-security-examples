package com.mygordienko.spring.security.examples.di;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class ClientSecurityConfiguration {

  @Bean
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) {
    http
      .authorizeHttpRequests(authz -> authz 
        .anyRequest().authenticated()
      )
      .oauth2Login(Customizer.withDefaults())
      ;

      return http.build();
  }

  @Bean
	public ClientRegistrationRepository clientRegistrationRepository(OAuth2ConfigurationProperties properties) {
		return new InMemoryClientRegistrationRepository(this.composeClientRegistrations(properties));
	}

	private List<ClientRegistration> composeClientRegistrations(OAuth2ConfigurationProperties properties) {
		
    return properties.getClients().stream()
      .<ClientRegistration>map(client -> {
      return ClientRegistration
        .withRegistrationId(client.getRegistrationId())
        .clientId(client.getId())
        .clientSecret(client.getSecret())
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .redirectUri(client.getRedirectUrl())
        .scope("openid", "profile")
        .authorizationUri(client.getAuthorizationUrl())
        .tokenUri(client.getTokenUrl())
        .userInfoUri(client.getUserInfoUrl())
        .userNameAttributeName(IdTokenClaimNames.SUB)
        .clientName(client.getName())
        .jwkSetUri(client.getJwksUrl())
        .build();
      })
      .toList();
	}

  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }
}
