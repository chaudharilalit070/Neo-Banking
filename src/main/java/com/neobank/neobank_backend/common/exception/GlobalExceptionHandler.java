package com.neobank.neobank_backend.common.exception;
import com.neobank.neobank_backend.common.api.ErrorResponse;
import com.neobank.neobank_backend.common.constants.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException exception
    ) {

        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                exception.getStatus().value(),
                exception.getErrorCode(),
                exception.getMessage(),
                MDC.get("correlationId"),
                null
        );

        return ResponseEntity
                .status(exception.getStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception
    ) {

        List<ErrorResponse.FieldError> fieldErrors =
                exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(this::mapFieldError)
                        .toList();

        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                400,
                ErrorCodes.VALIDATION_ERROR,
                "Request validation failed",
                MDC.get("correlationId"),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception exception
    ) {

        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                500,
                ErrorCodes.INTERNAL_ERROR,
                "An unexpected error occurred",
                MDC.get("correlationId"),
                null
        );

        return ResponseEntity
                .internalServerError()
                .body(response);
    }

    private ErrorResponse.FieldError mapFieldError(FieldError fieldError) {
        return new ErrorResponse.FieldError(
                fieldError.getField(),
                fieldError.getDefaultMessage()
        );
    }
}
package com.company.neobanking.customer.common.exception;

import com.company.neobanking.customer.common.api.ErrorResponse;
import com.company.neobanking.customer.common.constants.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                exception.getErrorCode(),
                exception.getMessage(),
                HttpStatus.NOT_FOUND,
                request
        );
    }


    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ConflictException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                exception.getErrorCode(),
                exception.getMessage(),
                HttpStatus.CONFLICT,
                request
        );
    }


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                exception.getErrorCode(),
                exception.getMessage(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return buildResponse(
                ErrorCodes.VALIDATION_ERROR,
                message,
                HttpStatus.BAD_REQUEST,
                request
        );
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                ErrorCodes.VALIDATION_ERROR,
                exception.getMessage(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                ErrorCodes.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }


    private ResponseEntity<ErrorResponse> buildResponse(
            String errorCode,
            String message,
            HttpStatus status,
            HttpServletRequest request
    ) {

        String correlationId =
                request.getHeader("X-Correlation-Id");

        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .errorCode(errorCode)
                        .message(message)
                        .status(status.value())
                        .path(request.getRequestURI())
                        .correlationId(correlationId)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }
}

@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException exception
) {

    Map<String, String> validationErrors = new HashMap<>();

    exception.getBindingResult()
            .getFieldErrors()
            .forEach(error ->
                    validationErrors.put(
                            error.getField(),
                            error.getDefaultMessage()
                    )
            );

    ErrorResponse response = ErrorResponse.builder()
            .success(false)
            .errorCode("VALIDATION_ERROR")
            .message("Validation failed")
            .status(HttpStatus.BAD_REQUEST.value())
            .validationErrors(validationErrors)
            .build();

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
}