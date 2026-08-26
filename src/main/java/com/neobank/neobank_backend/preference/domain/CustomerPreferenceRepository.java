package com.neobank.neobank_backend.preference.domain;

import java.util.Optional;
import java.util.UUID;

public interface CustomerPreferenceRepository {

    CustomerPreference save(CustomerPreference customerPreference);

    Optional<CustomerPreference> findByCustomerId(UUID customerId);

    boolean existsByCustomerId(UUID customerId);
}
