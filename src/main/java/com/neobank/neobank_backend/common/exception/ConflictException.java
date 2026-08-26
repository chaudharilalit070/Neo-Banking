package com.neobank.neobank_backend.common.exception;

import com.neobank.neobank_backend.common.constants.ErrorCodes;
import org.springframework.http.HttpStatus;

public class ConflictException extends BusinessException {

    public ConflictException(String message) {
        this(ErrorCodes.CONFLICT, message);
    }

    public ConflictException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.CONFLICT);
    }
}
