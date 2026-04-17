package com.mygordienko.spring.security.examples.oauth2client.common.api;

import java.util.List;

public record OffsetPageResponse<T>(
        List<T> items,
        long offset,
        int limit,
        long total
) {
}
