package com.mygordienko.spring.security.examples.di;

import java.util.List;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "oauth")
public class OAuth2ConfigurationProperties {
  
  @NestedConfigurationProperty
  private List<ClientProperties> clients = List.of();

  @Getter
  @Setter
  public static final class ClientProperties {
    private String id;
    private String secret;
    private String registrationId;
    private String name;
    private String redirectUrl;
    private Set<String> scopes = Set.of();
    private String authorizationUrl;
    private String tokenUrl;
    private String userInfoUrl;
    private String jwksUrl;
  }
}
