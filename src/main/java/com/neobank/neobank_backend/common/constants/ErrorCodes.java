package com.neobank.neobank_backend.common.constants;

public final class ErrorCodes {

    private ErrorCodes() {
    }

    public static final String VALIDATION_ERROR = "CUS-VAL-001";
    public static final String RESOURCE_NOT_FOUND = "CUS-GEN-404";
    public static final String CONFLICT = "CUS-GEN-409";
    public static final String BUSINESS_ERROR = "CUS-BIZ-001";
    public static final String INTERNAL_ERROR = "CUS-GEN-500";
    public static final String UNAUTHORIZED = "CUS-SEC-401";
    public static final String FORBIDDEN = "CUS-SEC-403";

    public static final String CUSTOMER_NOT_FOUND = "CUS-CUS-001";
    public static final String CUSTOMER_ALREADY_EXISTS = "CUS-CUS-002";
    public static final String INVALID_CUSTOMER_STATUS = "CUS-CUS-003";

    public static final String CONTACT_NOT_FOUND = "CUS-CON-001";
    public static final String CONTACT_ALREADY_EXISTS = "CUS-CON-002";
    public static final String CONTACT_DOES_NOT_BELONG_TO_CUSTOMER = "CUS-CON-003";

    public static final String ADDRESS_NOT_FOUND = "CUS-ADD-001";
    public static final String ADDRESS_ALREADY_EXISTS = "CUS-ADD-002";
    public static final String ADDRESS_DOES_NOT_BELONG_TO_CUSTOMER = "CUS-ADD-003";

    public static final String PREFERENCE_NOT_FOUND = "CUS-PRF-001";
    public static final String PREFERENCE_ALREADY_EXISTS = "CUS-PRF-002";

    public static final String CONSENT_NOT_FOUND = "CUS-CNT-001";
    public static final String CONSENT_ALREADY_WITHDRAWN = "CUS-CNT-002";

    public static final String LIFECYCLE_NOT_FOUND = "CUS-LFC-001";
    public static final String INVALID_LIFECYCLE_TRANSITION = "CUS-LFC-002";
}
