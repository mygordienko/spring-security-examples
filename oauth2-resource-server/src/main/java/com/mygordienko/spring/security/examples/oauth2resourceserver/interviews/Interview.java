package com.mygordienko.spring.security.examples.oauth2resourceserver.interviews;

import java.time.OffsetDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("interviews")
public record Interview(
        @Id Long id,
        OffsetDateTime timestamp,
        @Column("manager_email") String manager,
        @Column("applicant_email") String applicant,
        InterviewResult result
) {
}
