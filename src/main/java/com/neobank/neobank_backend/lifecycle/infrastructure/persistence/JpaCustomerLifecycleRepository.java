package com.neobank.neobank_backend.lifecycle.infrastructure.persistence;

import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaCustomerLifecycleRepository
        extends JpaRepository<CustomerLifecycle, Long> {

    Optional<CustomerLifecycle> findFirstByCustomer_IdOrderByEffectiveAtDescIdDesc(
            UUID customerId
    );

    List<CustomerLifecycle> findAllByCustomer_IdOrderByEffectiveAtAscIdAsc(
            UUID customerId
    );

    boolean existsByCustomer_Id(UUID customerId);
}
