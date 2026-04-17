package com.mygordienko.spring.security.examples.oauth2client.interviews;

import com.mygordienko.spring.security.examples.oauth2client.common.api.OffsetPageResponse;
import com.mygordienko.spring.security.examples.oauth2client.interviews.port.InterviewPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterviewQueryService {

    private final InterviewPort interviewPort;

    public OffsetPageResponse<Interview> findInterviews(long offset, int limit) {
        return interviewPort.findInterviews(offset, limit);
    }
}
