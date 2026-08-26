package com.neobank.neobank_backend.lifecycle.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerLifecycleRepository {

    CustomerLifecycle save(CustomerLifecycle lifecycle);

    Optional<CustomerLifecycle> findLatestByCustomerId(UUID customerId);

    List<CustomerLifecycle> findAllByCustomerId(UUID customerId);

    boolean existsByCustomerId(UUID customerId);
}
