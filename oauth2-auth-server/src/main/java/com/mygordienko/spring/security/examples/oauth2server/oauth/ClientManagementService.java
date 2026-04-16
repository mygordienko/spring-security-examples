package com.mygordienko.spring.security.examples.oauth2server.oauth;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mygordienko.spring.security.examples.oauth2server.oauth.adapter.RegisterClientRequest;
import com.mygordienko.spring.security.examples.oauth2server.oauth.port.StoredClientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientManagementService {

    private final StoredClientRepository storedClientRepository;
    private final RegisteredClientRepository registeredClientRepository;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public List<StoredClient> findAll() {
        return storedClientRepository.findAll();
    }

    @Transactional
    public void registerClient(RegisterClientRequest request) {
        StoredClient stored = new StoredClient();
        stored.setClientId(request.getClientId());
        stored.setClientSecret(request.getClientSecret());
        stored.setRedirectUri(request.getRedirectUri());
        stored.setScopes(request.getScopes());
        stored.setAuthorizationCode(request.isAuthorizationCode());
        stored.setClientCredentials(request.isClientCredentials());
        stored.setRequirePkce(request.isRequirePkce());
        storedClientRepository.save(stored);

        var builder = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(request.getClientId())
                .clientSecret(passwordEncoder.encode(request.getClientSecret()))
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(1))
                        .build());

        if (request.isAuthorizationCode()) {
            if (request.isRequirePkce()) {
                builder.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
            } else {
                builder.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
            }
            builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN);
            if (request.getRedirectUri() != null && !request.getRedirectUri().isBlank()) {
                builder.redirectUri(request.getRedirectUri());
            }
        }

        if (request.isClientCredentials()) {
            builder.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS);
        }

        if (request.getScopes() != null && !request.getScopes().isBlank()) {
            Arrays.stream(request.getScopes().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(builder::scope);
        }

        builder.clientSettings(ClientSettings.builder()
                .requireProofKey(request.isRequirePkce())
                .requireAuthorizationConsent(false)
                .build());

        registeredClientRepository.save(builder.build());
    }

    @Transactional
    public void deleteClient(String clientId) {
        // Remove from our tracking table
        storedClientRepository.deleteByClientId(clientId);
        // Remove from Spring AS JDBC tables directly
        jdbcTemplate.update("DELETE FROM oauth2_registered_client WHERE client_id = ?", clientId);
    }
}