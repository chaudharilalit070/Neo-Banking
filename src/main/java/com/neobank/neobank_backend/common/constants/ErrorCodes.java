package com.neobank.neobank_backend.common.constants;


public final class ErrorCodes {

    private ErrorCodes() {
    }

    // Validation
    public static final String VALIDATION_ERROR = "CUS-VAL-001";

    // Customer
    public static final String CUSTOMER_NOT_FOUND = "CUS-CUS-001";
    public static final String CUSTOMER_ALREADY_EXISTS = "CUS-CUS-002";
    public static final String INVALID_CUSTOMER_STATUS = "CUS-CUS-003";

    // Contact
    public static final String CONTACT_NOT_FOUND = "CUS-CON-001";
    public static final String CONTACT_ALREADY_EXISTS = "CUS-CON-002";

    // Address
    public static final String ADDRESS_NOT_FOUND = "CUS-ADD-001";

    // Preference
    public static final String PREFERENCE_NOT_FOUND = "CUS-PRF-001";

    // Consent
    public static final String CONSENT_NOT_FOUND = "CUS-CNT-001";

    // General
    public static final String RESOURCE_NOT_FOUND = "CUS-GEN-404";
    public static final String CONFLICT = "CUS-GEN-409";
    public static final String BUSINESS_ERROR = "CUS-BIZ-001";
    public static final String INTERNAL_ERROR = "CUS-GEN-500";
}