package com.mygordienko.spring.security.examples.oauth2resourceserver.interviews;

import com.mygordienko.spring.security.examples.oauth2resourceserver.common.api.OffsetPageResponse;
import com.mygordienko.spring.security.examples.oauth2resourceserver.interviews.port.InterviewRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterviewQueryService {

    private final InterviewRepository interviewRepository;

    public OffsetPageResponse<Interview> findInterviews(long offset, int limit) {
        List<Interview> interviews = interviewRepository.findPage(limit, offset);
        return new OffsetPageResponse<>(interviews, offset, limit, interviewRepository.count());
    }
}
