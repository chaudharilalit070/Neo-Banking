package com.neobank.neobank_backend.common.exception;

import com.neobank.neobank_backend.common.constants.ErrorCodes;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        this(ErrorCodes.RESOURCE_NOT_FOUND, message);
    }

    public ResourceNotFoundException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.NOT_FOUND);
    }
}
