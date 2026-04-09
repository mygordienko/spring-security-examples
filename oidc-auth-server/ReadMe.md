# OIDC Authorization server

* Using Spring Authorization Server:
  - [docs](https://docs.spring.io/spring-security/reference/servlet/oauth2/authorization-server/index.html)

* users configured in `application.yml`
* clients configured in `application.yml`
* OIDC auth server (authentication)
* NO authorization, this server is used to confirm identity, not to describe user permissions.


### Remember

* OAuth2 client authentication relies on cookies, and cookies are stored per *domain* without port. Also browsers treat
  "localhost" and "127.0.0.1" as the same domain. Therefore, it is better to have authorization server deployed on separate domain,
  or at least have an alias configures in *hosts* for 127.0.0.1 different from 'localhost'.

* *PasswordEncoder* is used not only for user passwords, but also for client secrets.

* Client registration requires `user-name-attribute` which might be different for different authorization servers. This
  attribute specifies the name of the "userName" field in the OIDC response. Spring default is "sub".