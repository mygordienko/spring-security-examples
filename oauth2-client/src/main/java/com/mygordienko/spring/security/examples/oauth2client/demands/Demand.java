package com.mygordienko.spring.security.examples.oauth2client.demands;

import java.time.OffsetDateTime;

public record Demand(
        OffsetDateTime timestamp,
        String author,
        DemandType type
) {
}
