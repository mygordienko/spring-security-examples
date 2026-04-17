package com.mygordienko.spring.security.examples.oauth2client.interviews.adapter;

import com.mygordienko.spring.security.examples.oauth2client.common.api.OffsetPageResponse;
import com.mygordienko.spring.security.examples.oauth2client.interviews.Interview;
import com.mygordienko.spring.security.examples.oauth2client.interviews.port.InterviewPort;
import com.mygordienko.spring.security.examples.oauth2client.resource.di.ResourceServerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ResourceServerInterviewAdapter implements InterviewPort {

    private static final ParameterizedTypeReference<OffsetPageResponse<Interview>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;
    private final ResourceServerProperties properties;

    @Override
    public OffsetPageResponse<Interview> findInterviews(long offset, int limit) {
        return restClient.get()
                .uri(properties.baseUrl() + "/interviews?offset={offset}&limit={limit}", offset, limit)
                .retrieve()
                .body(RESPONSE_TYPE);
    }
}
