package com.neobank.neobank_backend.lifecycle.domain;


import java.util.List;
import java.util.Optional;

public interface CustomerLifecycleRepository {

    /**
     * Save a customer lifecycle record.
     */
    CustomerLifecycle save(CustomerLifecycle lifecycle);


    /**
     * Find the latest lifecycle record for a customer.
     */
    Optional<CustomerLifecycle> findLatestByCustomerId(
            Long customerId
    );


    /**
     * Find complete lifecycle history for a customer.
     */
    List<CustomerLifecycle> findAllByCustomerId(
            Long customerId
    );


    /**
     * Check whether a customer has any lifecycle record.
     */
    boolean existsByCustomerId(
            Long customerId
    );
}