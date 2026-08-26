package com.neobank.neobank_backend.common.exception;

import com.neobank.neobank_backend.common.constants.ErrorCodes;
import org.springframework.http.HttpStatus;

public class ValidationException extends BusinessException {

    public ValidationException(String message) {
        this(ErrorCodes.VALIDATION_ERROR, message);
    }

    public ValidationException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.BAD_REQUEST);
    }
}
