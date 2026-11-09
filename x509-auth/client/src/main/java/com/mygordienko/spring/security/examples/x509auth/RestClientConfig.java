package com.mygordienko.spring.security.examples.x509auth;

import org.springframework.boot.restclient.autoconfigure.RestClientSsl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean
    public RestClient restClient(RestClient.Builder restClientBuilder, RestClientSsl restClientSsl) {
        return restClientBuilder
                .apply(restClientSsl.fromBundle("client-bundle"))
                .build();
    }

}
