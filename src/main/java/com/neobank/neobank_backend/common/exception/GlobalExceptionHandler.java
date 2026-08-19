//package com.neobank.neobank_backend.common.exception;
//import com.neobank.neobank_backend.common.api.ErrorResponse;
//import com.neobank.neobank_backend.common.constants.ErrorCodes;
//import jakarta.servlet.http.HttpServletRequest;
//import org.slf4j.MDC;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import java.time.Instant;
//import java.util.List;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(BusinessException.class)
//    public ResponseEntity<ErrorResponse> handleBusinessException(
//            BusinessException exception
//    ) {
//
//        ErrorResponse response = new ErrorResponse(
//                Instant.now(),
//                exception.getStatus().value(),
//                exception.getErrorCode(),
//                exception.getMessage(),
//                MDC.get("correlationId"),
//                null
//        );
//
//        return ResponseEntity
//                .status(exception.getStatus())
//                .body(response);
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponse> handleValidationException(
//            MethodArgumentNotValidException exception
//    ) {
//
//        List<ErrorResponse.FieldError> fieldErrors =
//                exception.getBindingResult()
//                        .getFieldErrors()
//                        .stream()
//                        .map(this::mapFieldError)
//                        .toList();
//
//        ErrorResponse response = new ErrorResponse(
//                Instant.now(),
//                400,
//                ErrorCodes.VALIDATION_ERROR,
//                "Request validation failed",
//                MDC.get("correlationId"),
//                fieldErrors
//        );
//
//        return ResponseEntity.badRequest().body(response);
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleUnexpectedException(
//            Exception exception
//    ) {
//
//        ErrorResponse response = new ErrorResponse(
//                Instant.now(),
//                500,
//                ErrorCodes.INTERNAL_ERROR,
//                "An unexpected error occurred",
//                MDC.get("correlationId"),
//                null
//        );
//
//        return ResponseEntity
//                .internalServerError()
//                .body(response);
//    }
//
//    private ErrorResponse.FieldError mapFieldError(FieldError fieldError) {
//        return new ErrorResponse.FieldError(
//                fieldError.getField(),
//                fieldError.getDefaultMessage()
//        );
//    }
//}
