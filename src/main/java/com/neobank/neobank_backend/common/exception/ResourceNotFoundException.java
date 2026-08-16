package com.neobank.neobank_backend.common.exception;


import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(
            String errorCode,
            String message
    ) {
        super(errorCode, message, HttpStatus.NOT_FOUND);
    }
}