package com.neobank.neobank_backend.lifecycle.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaCustomerLifecycleRepository
        extends JpaRepository<CustomerLifecycle, Long> {

    /**
     * Find the latest lifecycle record for a customer.
     *
     * Ordering:
     * 1. Latest effective time
     * 2. Highest ID as deterministic tie-breaker
     */
    Optional<CustomerLifecycle> findFirstByCustomer_IdOrderByEffectiveAtDescIdDesc(
            Long customerId
    );

    /**
     * Find complete lifecycle history.
     *
     * Oldest record first.
     */
    List<CustomerLifecycle> findAllByCustomer_IdOrderByEffectiveAtAscIdAsc(
            Long customerId
    );

    /**
     * Check whether lifecycle records exist for a customer.
     */
    boolean existsByCustomer_Id(
            Long customerId
    );
}