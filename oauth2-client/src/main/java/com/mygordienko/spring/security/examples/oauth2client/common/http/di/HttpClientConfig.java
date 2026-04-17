package com.mygordienko.spring.security.examples.oauth2client.common.http.di;

import com.mygordienko.spring.security.examples.oauth2client.common.http.oauth2.OAuth2BearerTokenInterceptor;
import com.mygordienko.spring.security.examples.oauth2client.resource.di.ResourceServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(ResourceServerProperties.class)
public class HttpClientConfig {

    @Bean
    public RestClient restClient(OAuth2BearerTokenInterceptor oauth2BearerTokenInterceptor) {
        return RestClient.builder()
                .requestInterceptor(oauth2BearerTokenInterceptor)
                .build();
    }
}
