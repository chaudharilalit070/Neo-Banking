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

package com.company.neobanking.customer.common.exception;

import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {

    private final String errorCode;

    public ConflictException(
            String errorCode,
            String message
    ) {
        super(message);
        this.errorCode = errorCode;
    }
}