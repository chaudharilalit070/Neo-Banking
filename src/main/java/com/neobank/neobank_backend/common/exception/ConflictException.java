package com.neobank.neobank_backend.common.exception;


import org.springframework.http.HttpStatus;

public class ConflictException extends BusinessException {

    public ConflictException(
            String errorCode,
            String message
    ) {
        super(errorCode, message, HttpStatus.CONFLICT);
    }
}