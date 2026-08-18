package com.neobank.neobank_backend.customer.domain;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(UUID customerId);

    boolean existsByCustomerNumber(String customerNumber);
}