package com.mygordienko.spring.security.examples.oauth2client.interviews;

import java.time.OffsetDateTime;

public record Interview(
        OffsetDateTime timestamp,
        String manager,
        String applicant,
        InterviewResult result
) {
}
