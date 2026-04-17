package com.mygordienko.spring.security.examples.oauth2resourceserver.common.api;

import java.util.List;

public record OffsetPageResponse<T>(
        List<T> items,
        long offset,
        int limit,
        long total
) {
}
