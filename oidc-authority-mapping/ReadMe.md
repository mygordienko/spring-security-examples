# OIDC login with local authority mapping

Demonstrates the **federated identity vs. local authorization** split: users log in through an external
OpenID Provider, but what they are allowed to do inside the application is decided by the application's
own account table.

The analogy is a "Login with GitHub"-style button: the provider tells you *who* the user is, it does not
tell you (and you should not trust it to tell you) *what the user may do* in your application.

## Modules

| Module     | Port | Role                                                                                     |
|------------|------|------------------------------------------------------------------------------------------|
| `provider` | 9100 | OpenID Provider built with Spring Authorization Server. Form login, no roles, no claims about permissions. |
| `client`   | 8082 | Spring MVC + Thymeleaf web application. OIDC login, local subject → authorities mapping.   |

Both modules are leaves; the `oidc-authority-mapping` directory itself is a java-only aggregator.

## Prerequisites: hosts alias

Cookies are scoped by **host** and ignore the port (RFC 6265), so two applications served from
`127.0.0.1:9100` and `127.0.0.1:8082` would both use a cookie named `JSESSIONID` for the same host and
overwrite each other's session. Giving the provider a separate host name, `auth.local`, isolates the
sessions.

The provider registers exactly one redirect URI,
`http://127.0.0.1:8082/login/oauth2/code/authority-mapping-client`, so the browser must come back to
`127.0.0.1`; `localhost` is a different host and would not match the registration.

Add to `/etc/hosts`:

```
127.0.0.1 auth.local
```

Then always open the client at `http://127.0.0.1:8082/`. (Opening `http://localhost:8082/` also works:
`LoopbackIpRedirectFilter` rewrites the request to `127.0.0.1`.)

As an alternative to the hosts alias, each application could use a distinct session-cookie name
(`server.servlet.session.cookie.name`), so the two `JSESSIONID` cookies would no longer collide.

## Running

Start the provider first, then the client, each in its own terminal:

```bash
./gradlew :oidc-authority-mapping:provider:bootRun
```

```bash
./gradlew :oidc-authority-mapping:client:bootRun
```

Open <http://127.0.0.1:8082/>.

Ports `9100`/`8082` were chosen so this example can run next to the other modules of this repository
(`9000`, `9001`, `8080` are already used by `oidc-auth-server`, `oauth2-resource-server`,
`oidc-simple-client` and `oauth2-client`).

## Users and access

The provider knows these login/password pairs (`provider/src/main/resources/application.yml`):

| Login   | Password | Provider roles |
|---------|----------|----------------|
| `userA` | `pass`   | none           |
| `userB` | `pass`   | none           |

The client knows these local accounts, keyed by OIDC subject
(the `app.local-users` list in `client/src/main/resources/application.yml`):

| OIDC subject | Local authorities          |
|--------------|----------------------------|
| `userA`      | `ROLE_USER`                |
| `userB`      | `ROLE_USER`, `ROLE_ADMIN`  |

Each local account must define at least one authority; the application fails at startup on an empty
local-account table, a duplicate subject, or an account without authorities.

Resulting access:

| Endpoint      | Required authority | `userA` | `userB`   |
|---------------|--------------------|---------|-----------|
| `/`           | public             | yes     | yes       |
| `/login`      | public             | yes     | yes       |
| `/user`       | `ROLE_USER`        | yes     | yes       |
| `/restricted` | `ROLE_ADMIN`       | 403     | yes       |

Any other OIDC subject (i.e. a provider user without a local account) is rejected at login time instead
of being silently treated as an authenticated user.

## Flow

1. Anonymous user opens the client at `/` and clicks "Log in".
2. The client redirects to the provider's authorization endpoint
   (`http://auth.local:9100/oauth2/authorize`) with PKCE parameters.
3. The provider authenticates `userA`/`userB` with its own form login and returns an ID token whose
   `sub` claim is the login name. The token carries identity only.
4. The client's `LocalAuthorityOidcUserService` delegates to the standard `OidcUserService`, takes the
   `sub` claim, looks it up in the local account table and rebuilds the principal with **only** the local
   authorities. Authorities coming from the provider are dropped.
5. Unknown `sub` → `OAuth2AuthenticationException` → redirected to `/login?error=true`.
6. Authorization is evaluated on local authorities only: `/restricted` needs `ROLE_ADMIN`.

The trust boundary therefore is:

```
provider  ->  identity (sub, standard OIDC claims)
client    ->  authorization (ROLE_USER / ROLE_ADMIN from its own account table)
```

## Key files

Provider:

* `provider/.../di/AuthorizationServerConfiguration.java` - two filter chains (authorization server
  endpoints, form login), in-memory users, JWK source, no `OAuth2TokenCustomizer`.
* `provider/.../di/UserCredentialsConfigurationProperties.java` - `credentials.users`, login/password only.

Client:

* `client/.../security/LocalAuthorityOidcUserService.java` - replaces provider authorities with local
  ones and rejects unknown subjects.
* `client/.../security/LocalUserAuthorities.java` - local subject → authorities lookup.
* `client/.../di/LocalUserAuthoritiesProperties.java` - `app.local-users` configuration.
* `client/.../di/ClientSecurityConfiguration.java` - `oauth2Login` + PKCE + authorization rules.
* `client/.../web/LoopbackIpRedirectFilter.java` - rewrites `localhost` to `127.0.0.1`.

## Things to note

* **PKCE is required by this provider.** `require-proof-key: true` is set explicitly on the registered
  client, so the authorization code request must carry a `code_challenge`. The client enables it via
  `OAuth2AuthorizationRequestCustomizers.withPkce()` on its `DefaultOAuth2AuthorizationRequestResolver`.
  Without it the provider rejects the authorization request.

* **No issuer discovery.** Like the other OIDC modules of this repository, both sides use explicit
  endpoint URIs instead of `issuer-uri`, so nothing needs a reachable discovery document at startup.
  The trade-off is that the client does not validate the ID token's `iss` claim. In a real application,
  either configure `issuer-uri` or validate the issuer explicitly, and key local accounts by
  `(issuer, sub)` rather than by `sub` alone: `sub` is only unique within one issuer.

* **`user-name-attribute: sub`.** The client configures it explicitly because authorization servers differ
  here. The local lookup itself always keys on the ID token's `sub` claim, regardless of this setting.

* **`PasswordEncoder` is used for client secrets too.** The provider uses `NoOpPasswordEncoder`, so
  `client-secret: secret` is stored and compared in clear text. This is a demo-only choice and must never
  be used in a real application - and both the client and the provider have to agree on the encoder.

* **Logout is local.** The client invalidates its own session only; the provider session (and therefore the
  SSO state in the browser) survives, which is normal SSO behaviour. RP-initiated logout at the provider's
  `end_session_endpoint` would be a separate feature.

* **The provider is identity-only.** It has no roles, no authorities claim and no token customizer on
  purpose. Trusting provider-supplied authorities and then mapping them to local ones is exactly the
  mistake this example avoids: a client must never grant permissions based on an external system's view
  of the user.

## Validation

Unit tests cover the local authority lookup and the trust boundary: known subjects receive only their
local authorities (provider authorities are dropped), and an unknown subject raises
`OAuth2AuthenticationException`.

```bash
./gradlew :oidc-authority-mapping:client:test
```
