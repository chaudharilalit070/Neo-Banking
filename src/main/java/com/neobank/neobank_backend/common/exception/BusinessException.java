package com.neobank.neobank_backend.common.exception;

import com.neobank.neobank_backend.common.constants.ErrorCodes;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus status;

    public BusinessException(String message) {
        this(ErrorCodes.BUSINESS_ERROR, message, HttpStatus.BAD_REQUEST);
    }

    public BusinessException(String errorCode, String message) {
        this(errorCode, message, HttpStatus.BAD_REQUEST);
    }

    public BusinessException(String errorCode, String message, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCodes.BUSINESS_ERROR;
        this.status = HttpStatus.BAD_REQUEST;
    }
}
