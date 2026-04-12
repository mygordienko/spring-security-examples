# Simple login form

* Spring default login form with username and password
* Simple Welcome page that shows username of the active user passed from backend
* In-Memory user storage
* Redirect to specific page after login
* Logout button


## 999 Error after login

### How it looks: 

After authentication completes getting redirected to `/error?continue` and getting response:

```
{"timestamp":"2026-04-12T17:53:55.275Z","status":999,"error":"None"}
```

### What happens


* Browser, along with the main request for `/`, makes background request for `favicon.ico` which is not present in the project.

* That requests results in 404 and Spring sends back redirect to `/error`, which is protected.

* Browser tries to access `/error` which results in redirecting to `/login`, but original request to `/error` gets saved, so the user can be redirected back to it after login, this is a Spring feature. It can be effectively disabled by configuring `.defaultSuccessUrl("/", true)`, which tell Spring always redirect to specified page after login, ignoring saved requests.

* after login Spring redirects to saved `/error` which does not have configured controller, that results in 999 error. If there were such controller we would be redirected to error page.

**NOTE:** It is important that this error can happen with any request to the missing resource before authentication, request to /favico.ico is just a common case, because it is kind of convention and browsers always try to get it.

### How to avoid

Yhere are several options:

* Enable `permitAll()` for `/error`

* Disable request cache for `/error`:

```
@Bean
  public RequestCache requestCache() {
    HttpSessionRequestCache cache = new HttpSessionRequestCache();
    cache.setRequestMatcher(request ->
        !request.getRequestURI().equals("/error")
    );
    return cache;
  }
```

* Add missing resource, in this case: `favicon.ico`

* Configure `.defaultSuccessUrl("/", true)` (with `true`), **BUT** that will not allow rediret to saved requests after login. Ex. User hits `/protected` -> gets redirected to `/login` -> logins -> redirected to `/protected`, instead user will always be redirected to specified endpoint.