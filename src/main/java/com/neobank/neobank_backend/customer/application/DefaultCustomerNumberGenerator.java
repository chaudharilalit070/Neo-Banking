package com.neobank.neobank_backend.customer.application;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DefaultCustomerNumberGenerator
        implements CustomerNumberGenerator {

    @Override
    public String generate() {
        return "CUS-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 16)
                        .toUpperCase();
    }
}