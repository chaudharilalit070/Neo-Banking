package com.neobank.neobank_backend.preference.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaCustomerPreferenceRepository
        extends JpaRepository<CustomerPreference, Long> {

    Optional<CustomerPreference> findByCustomerId(Long customerId);

    boolean existsByCustomerId(Long customerId);
}