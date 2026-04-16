package com.mygordienko.spring.security.examples.oauth2server.oauth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "registered_clients")
@Getter
@Setter
@NoArgsConstructor
public class StoredClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String clientId;

    @Column(nullable = false)
    private String clientSecret;

    private String redirectUri;

    private String scopes;

    private boolean authorizationCode;

    private boolean clientCredentials;

    private boolean requirePkce;
}