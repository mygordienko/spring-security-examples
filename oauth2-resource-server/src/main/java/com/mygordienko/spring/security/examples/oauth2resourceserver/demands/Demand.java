package com.mygordienko.spring.security.examples.oauth2resourceserver.demands;

import java.time.OffsetDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("demands")
public record Demand(
        @Id Long id,
        OffsetDateTime timestamp,
        @Column("author_email") String author,
        DemandType type
) {
}
