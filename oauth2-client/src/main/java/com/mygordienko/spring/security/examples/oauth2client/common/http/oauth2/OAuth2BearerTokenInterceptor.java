package com.mygordienko.spring.security.examples.oauth2client.common.http.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2BearerTokenInterceptor implements ClientHttpRequestInterceptor {

    private static final String REGISTRATION_ID = "pkce-client";

    private final OAuth2AuthorizedClientRepository authorizedClientRepository;

    @Override
    public ClientHttpResponse intercept(
            org.springframework.http.HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution
    ) throws IOException {
        if (!request.getHeaders().containsHeader(HttpHeaders.AUTHORIZATION)) {
            resolveAccessToken().ifPresent(token ->
                    request.getHeaders().setBearerAuth(token)
            );
        }

        return execution.execute(request, body);
    }

    private Optional<String> resolveAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return Optional.empty();
        }

        HttpServletRequest request = attributes.getRequest();
        OAuth2AuthorizedClient authorizedClient = authorizedClientRepository.loadAuthorizedClient(
                REGISTRATION_ID,
                authentication,
                request
        );

        if (authorizedClient == null || authorizedClient.getAccessToken() == null) {
            return Optional.empty();
        }

        return Optional.of(authorizedClient.getAccessToken().getTokenValue());
    }
}
