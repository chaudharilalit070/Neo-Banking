package com.neobank.neobank_backend.common.util;

public final class MaskingUtil {

    private MaskingUtil() {
    }

    public static String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return "***" + email.substring(Math.max(0, atIndex));
        }
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);

        if (localPart.length() <= 2) {
            return localPart.charAt(0) + "***" + domainPart;
        }
        return localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1) + domainPart;
    }

    public static String maskPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return phone;
        }
        String trimmed = phone.trim();
        if (trimmed.length() <= 4) {
            return "****";
        }
        int visibleDigits = 4;
        String maskedPrefix = "*".repeat(trimmed.length() - visibleDigits);
        return maskedPrefix + trimmed.substring(trimmed.length() - visibleDigits);
    }

    public static String maskName(String name) {
        if (name == null || name.isBlank()) {
            return name;
        }
        String trimmed = name.trim();
        if (trimmed.length() <= 2) {
            return trimmed.charAt(0) + "*";
        }
        return trimmed.charAt(0) + "*".repeat(trimmed.length() - 2) + trimmed.charAt(trimmed.length() - 1);
    }

    public static String maskGeneric(String value, int visibleSuffixCount) {
        if (value == null || value.isBlank()) {
            return value;
        }
        int len = value.length();
        if (len <= visibleSuffixCount) {
            return "*".repeat(len);
        }
        return "*".repeat(len - visibleSuffixCount) + value.substring(len - visibleSuffixCount);
    }
}
