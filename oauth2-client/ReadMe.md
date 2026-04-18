# OAuth2 client

Client Web application (BFF) that authorize users with OAuth2 Authorization Server and does 'session-to-JWT' relay.

## Features

* Thymeleaf UI with restricted sections
* async calls to backend (not necessary here, just for demonstration)
* session-to-JWT relay using Interceptor for RestClient
  * Does use refresh token
* OAuth2 login
* Logout

## Things to note:

* Refresh token support is mostly a client-side configuration. On server side (see 'oauth2-auth-server') 
    we just need to enable `AuthorizationGrantType.REFRESH_TOKEN` and configure TTL

* Clients, by default, do not use refresh tokens. The default setup, which is just an interceptor that loads 
    OAuth2AuthorizedClient using authentication object and then takes stored access token from it, won't automatically 
    update the token once it's expired. Renewal must be configured explicitly:

  ```
  @Bean
  public OAuth2AuthorizedClientManager oauth2AuthorizedClientManager(
          ClientRegistrationRepository clientRegistrationRepository,
          OAuth2AuthorizedClientRepository authorizedClientRepository
  ) {
      OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
              .refreshToken()
              .build();

      DefaultOAuth2AuthorizedClientManager manager =
              new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
      manager.setAuthorizedClientProvider(authorizedClientProvider);
      return manager;
  }
  ```
    
    and then, in our Interceptor, instead of:

  ```
  HttpServletRequest request = attributes.getRequest();
  OAuth2AuthorizedClient authorizedClient = authorizedClientRepository.loadAuthorizedClient(
          REGISTRATION_ID,
          authentication,
          request
  );
  ```
  
    we have:

  ```
  HttpServletRequest request = attributes.getRequest();
  HttpServletResponse response = attributes.getResponse();
  OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
          .withClientRegistrationId(REGISTRATION_ID)
          .principal(authentication)
          .attribute(HttpServletRequest.class.getName(), request)
          .attribute(HttpServletResponse.class.getName(), response)
          .build();

  OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

  ```
  
    **NOTE:** Spring convention over "re-authorization" is that if the access_token is not expired - return the same
        authorized client, so even if it looks like authorization on each request, under the hood it calls OAuth2 server
        only when access_token is expired.