package com.neobank.neobank_backend.preference.infrastructure.persistence;

import com.neobank.neobank_backend.preference.domain.CustomerPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaCustomerPreferenceRepository
        extends JpaRepository<CustomerPreference, Long> {

    Optional<CustomerPreference> findByCustomerId(UUID customerId);

    boolean existsByCustomerId(UUID customerId);
}
