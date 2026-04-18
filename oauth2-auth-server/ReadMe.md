# OAuth2 Authorization server

Based on Spring Authorization Server

## Features

* Custom login page
* Logout option
* Built-in users
* Two user roles: `admin`, `regular`
* Simple client management:
  * UI to add/delete clients
  * available only to admins
  * PostgreSQL storage for clients data
* JWT access tokens
* Refresh token, ttl is configurable via application.yml 

## Notes

### Client authentication type

Different strategies can be used for different clients, ex. depending on whether the client supports PKCE or not.

```
if (request.isAuthorizationCode()) {
      if (request.isRequirePkce()) {
          builder.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
      } else {
          // Something else
      }
      builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
              .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN);
      if (request.getRedirectUri() != null && !request.getRedirectUri().isBlank()) {
          builder.redirectUri(request.getRedirectUri());
      }
 }
```

### JWT token serialization problem
  
**General rule:** where Jackson serializes Object-typed values with polymorphic type 
information, always use well-known, concrete collection types (ArrayList, HashMap, HashSet, etc.).

Example: when defining JWT customizer use well-known Java types (ex. ArrayList), 
not internal types (collectorls.toList()).

Spring does serialize security classes in JSON, but keeps type information (`@class` field).
For example when serializing authorities collection, if its type is  `java.util.ImmutableCollections$ListN`
(toList()) there will be an exception on reading it back (ex when a client calls UserInfo endpoint)

```
@Bean
 public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
     return context -> {
         if (context.getPrincipal() != null && context.getPrincipal().getAuthorities() != null) {
             var authorities = context.getPrincipal().getAuthorities()
                     .stream()
                     .map(a -> a.getAuthority())
                     .collect(Collectors.toCollection(ArrayList::new)); // NOTE: Do not use toList
             context.getClaims().claim("authorities", authorities);
         }
     };
 }
```