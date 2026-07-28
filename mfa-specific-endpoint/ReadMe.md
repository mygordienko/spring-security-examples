# MFA for admin-related endpoints

Scenario: protect admin-related endpoints with additional layer of authentication (OTT)

## Features

* Spring-generated login form and token submit form
* In-memory user details service
* Redirect to specific page after login
* Redirect to specific page after token has been generated
* **NOTE:** No email server configured, token gets written to the console
* MFA (see. `@EnableMultiFactorAuthentication`)
  * configured only for admin-related endpoints 
  * users can login with password, admin users see admin links but clicking on them will trigger additional check for OTT
  * on admin page there is an action button that is additionally guarded by checking if OTT login was recent enough
    * this check is independent of token TTL


## Things to note:

* Usage of `AuthorizationManagerFactories` vs `AllRequiredFactorsAuthorizationManager`
