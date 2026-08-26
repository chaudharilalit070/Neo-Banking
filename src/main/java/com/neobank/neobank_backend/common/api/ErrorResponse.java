package com.neobank.neobank_backend.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ErrorResponse(
        Instant timestamp,
        int status,
        String errorCode,
        String message,
        String path,
        String correlationId,
        List<FieldError> fieldErrors,
        Map<String, String> validationErrors
) {

    public record FieldError(
            String field,
            String message
    ) {
    }

    public static ErrorResponse of(
            int status,
            String errorCode,
            String message,
            String path,
            String correlationId
    ) {
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status)
                .errorCode(errorCode)
                .message(message)
                .path(path)
                .correlationId(correlationId)
                .build();
    }
}
