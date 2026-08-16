package com.neobank.neobank_backend.common.api;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        Instant timestamp,
        String correlationId,
        T data
) {

    public static <T> ApiResponse<T> success(
            String correlationId,
            T data
    ) {
        return new ApiResponse<>(
                Instant.now(),
                correlationId,
                data
        );
    }
}