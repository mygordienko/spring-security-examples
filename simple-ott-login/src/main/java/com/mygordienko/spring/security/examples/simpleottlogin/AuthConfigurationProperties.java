package com.mygordienko.spring.security.examples.simpleottlogin;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "auth")
public class AuthConfigurationProperties {

  @Setter
  @Getter
  @NestedConfigurationProperty
  private List<UserAuth> users = List.of();

  @Getter
  @Setter
  public static class UserAuth {
    private String email;
    private String password;
    private List<String> roles = List.of();
  }
}
