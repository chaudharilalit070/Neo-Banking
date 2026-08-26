package com.neobank.neobank_backend.preference.infrastructure.persistence;

import com.neobank.neobank_backend.preference.domain.CustomerPreference;
import com.neobank.neobank_backend.preference.domain.CustomerPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CustomerPreferenceRepositoryAdapter
        implements CustomerPreferenceRepository {

    private final JpaCustomerPreferenceRepository jpaCustomerPreferenceRepository;

    @Override
    public CustomerPreference save(CustomerPreference customerPreference) {
        return jpaCustomerPreferenceRepository.save(customerPreference);
    }

    @Override
    public Optional<CustomerPreference> findByCustomerId(UUID customerId) {
        return jpaCustomerPreferenceRepository.findByCustomerId(customerId);
    }

    @Override
    public boolean existsByCustomerId(UUID customerId) {
        return jpaCustomerPreferenceRepository.existsByCustomerId(customerId);
    }
}
