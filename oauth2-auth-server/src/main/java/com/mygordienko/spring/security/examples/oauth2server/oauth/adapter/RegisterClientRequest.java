package com.mygordienko.spring.security.examples.oauth2server.oauth.adapter;

import lombok.Data;

@Data
public class RegisterClientRequest {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String scopes;        // comma-separated, e.g. "openid,profile,read"
    private boolean authorizationCode;
    private boolean clientCredentials;
    private boolean requirePkce;
}