# OAuth2 Resource Server

## Features

* Two REST endpoints for different user roles
* Expect JWT token (No introspection needed)
* works with 'oauth2-auth-server'
* JDBC storage

## Things to note

* Since `oauth2-auth-server` uses custom claim name for authorities (default is 'sub') we have to reflect 
  that customization.

```
    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("authorities");
        authoritiesConverter.setAuthorityPrefix("");
    
        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return authenticationConverter;
    }
```

```
    .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
    );
```