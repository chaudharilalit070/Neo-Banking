package com.neobank.neobank_backend.preference.domain;


import java.util.Optional;

public interface CustomerPreferenceRepository {

    CustomerPreference save(CustomerPreference customerPreference);

    Optional<CustomerPreference> findByCustomerId(Long customerId);

    boolean existsByCustomerId(Long customerId);
}