# OTT login

Demonstrates a simple scenario with one-time token.

## Features

* Spring-generated login form and token submit form
* In-memory user details service
* Redirect to specific page after login
* Redirect to specific page after token has been generated
* **NOTE:** No email server configured, token gets written to the console


## Things to note:

* `Customizer<HttpSecurity>` is used to add additional login option to existing configuration (form login)
* it is OK to have `/ott/sent` open just like `/login`. The configrmation message though should be generic: "If your account exists, you will be notified shortly", that prevents *user enumeration*.