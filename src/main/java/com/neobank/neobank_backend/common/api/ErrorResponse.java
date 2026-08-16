package com.neobank.neobank_backend.common.api;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        Instant timestamp,
        int status,
        String errorCode,
        String message,
        String correlationId,
        List<FieldError> fieldErrors
) {

    public record FieldError(
            String field,
            String message
    ) {
    }
}