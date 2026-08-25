package com.neobank.neobank_backend.preference.infrastructure.persistence;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerPreferenceRepositoryAdapter
        implements CustomerPreferenceRepository {

    private final JpaCustomerPreferenceRepository
            jpaCustomerPreferenceRepository;


    @Override
    public CustomerPreference save(
            CustomerPreference customerPreference
    ) {
        return jpaCustomerPreferenceRepository.save(
                customerPreference
        );
    }


    @Override
    public Optional<CustomerPreference> findByCustomerId(
            Long customerId
    ) {
        return jpaCustomerPreferenceRepository
                .findByCustomerId(customerId);
    }


    @Override
    public boolean existsByCustomerId(
            Long customerId
    ) {
        return jpaCustomerPreferenceRepository
                .existsByCustomerId(customerId);
    }
}