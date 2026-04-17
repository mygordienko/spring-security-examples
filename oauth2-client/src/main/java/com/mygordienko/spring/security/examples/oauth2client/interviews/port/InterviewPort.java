package com.mygordienko.spring.security.examples.oauth2client.interviews.port;

import com.mygordienko.spring.security.examples.oauth2client.common.api.OffsetPageResponse;
import com.mygordienko.spring.security.examples.oauth2client.interviews.Interview;

public interface InterviewPort {

    OffsetPageResponse<Interview> findInterviews(long offset, int limit);
}
