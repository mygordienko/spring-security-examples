# OTT login

Demonstrates a simple scenario with one-time token.

## Features

* Spring-generated login form and token submit form
* In-memory user details service
* Redirect to specific page after login
* Redirect to specific page after token has been generated
* **NOTE:** No email server configured, token gets written to the console
* MFA (see. `@EnableMultiFactorAuthentication`)

## Things to note:

* `Customizer<HttpSecurity>` is used to add additional login option to existing configuration (form login)
* it is OK to have `/ott/sent` open just like `/login`. The configrmation message though should be generic: "If your account exists, you will be notified shortly", that prevents *user enumeration*.
* Authorities (auth methods) specified in `@EnableMultiFactorAuthentication` can be used in any order (password, then ott or another way around). If the same authority is specified multiple times it gets merged (like Set). If `@EnableMultiFactorAuthentication` used, auth methods that are not listed in it won't work, ex. if only OTT is specified than password login results in 403, even if the login form is shown.