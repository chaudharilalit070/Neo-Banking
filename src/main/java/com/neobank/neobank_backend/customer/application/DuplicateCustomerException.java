package com.neobank.neobank_backend.customer.application;


public class DuplicateCustomerException extends RuntimeException {

    public DuplicateCustomerException(String customerNumber) {
        super("Customer already exists: " + customerNumber);
    }
}