package com.neobank.neobank_backend.customer.infrastructure.persistence;


import com.neobank.neobank_backend.customer.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaCustomerRepository extends JpaRepository<Customer, UUID> {

    boolean existsByCustomerNumber(String customerNumber);
}