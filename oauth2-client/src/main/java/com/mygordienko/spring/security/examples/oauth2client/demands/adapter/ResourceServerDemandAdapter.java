package com.mygordienko.spring.security.examples.oauth2client.demands.adapter;

import com.mygordienko.spring.security.examples.oauth2client.common.api.OffsetPageResponse;
import com.mygordienko.spring.security.examples.oauth2client.demands.Demand;
import com.mygordienko.spring.security.examples.oauth2client.demands.port.DemandPort;
import com.mygordienko.spring.security.examples.oauth2client.resource.di.ResourceServerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ResourceServerDemandAdapter implements DemandPort {

    private static final ParameterizedTypeReference<OffsetPageResponse<Demand>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;
    private final ResourceServerProperties properties;

    @Override
    public OffsetPageResponse<Demand> findDemands(long offset, int limit) {
        return restClient.get()
                .uri(properties.baseUrl() + "/demands?offset={offset}&limit={limit}", offset, limit)
                .retrieve()
                .body(RESPONSE_TYPE);
    }
}
