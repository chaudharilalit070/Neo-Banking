package com.neobank.neobank_backend.common.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus status;

    public BusinessException(
            String errorCode,
            String message,
            HttpStatus status
    ) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}
