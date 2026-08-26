package com.neobank.neobank_backend.customer.infrastructure.persistence;

import com.neobank.neobank_backend.customer.domain.Customer;
import com.neobank.neobank_backend.customer.domain.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final JpaCustomerRepository jpaCustomerRepository;

    @Override
    public Customer save(Customer customer) {
        return jpaCustomerRepository.save(customer);
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        return jpaCustomerRepository.findById(customerId);
    }

    @Override
    public boolean existsById(UUID customerId) {
        return jpaCustomerRepository.existsById(customerId);
    }

    @Override
    public boolean existsByCustomerNumber(String customerNumber) {
        return jpaCustomerRepository.existsByCustomerNumber(customerNumber);
    }
}