package com.neobank.neobank_backend.common.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends BusinessException {

    public ValidationException(
            String errorCode,
            String message
    ) {
        super(errorCode, message, HttpStatus.BAD_REQUEST);
    }
}
