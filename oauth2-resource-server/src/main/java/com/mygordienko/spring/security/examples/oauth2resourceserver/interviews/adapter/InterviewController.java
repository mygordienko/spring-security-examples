package com.mygordienko.spring.security.examples.oauth2resourceserver.interviews.adapter;

import com.mygordienko.spring.security.examples.oauth2resourceserver.common.api.OffsetPageResponse;
import com.mygordienko.spring.security.examples.oauth2resourceserver.interviews.Interview;
import com.mygordienko.spring.security.examples.oauth2resourceserver.interviews.InterviewQueryService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewQueryService interviewQueryService;

    @GetMapping("/interviews")
    public OffsetPageResponse<Interview> getInterviews(
            @RequestParam(defaultValue = "0") @Min(0) long offset,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit
    ) {
        return interviewQueryService.findInterviews(offset, limit);
    }
}
