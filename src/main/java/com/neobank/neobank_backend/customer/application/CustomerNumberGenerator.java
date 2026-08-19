package com.neobank.neobank_backend.customer.application;

import java.util.UUID;

public interface CustomerNumberGenerator {

    String generate();

    class CustomerNotFoundException extends RuntimeException {

        public CustomerNotFoundException(UUID customerId) {
            super("Customer not found: " + customerId);
        }
    }
}