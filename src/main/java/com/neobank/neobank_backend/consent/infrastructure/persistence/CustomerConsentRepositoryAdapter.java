package com.neobank.neobank_backend.consent.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerConsentRepositoryAdapter
        implements CustomerConsentRepository {

    private final JpaCustomerConsentRepository
            jpaCustomerConsentRepository;


    /**
     * Save a new consent history record.
     */
    @Override
    public CustomerConsent save(CustomerConsent customerConsent) {

        return jpaCustomerConsentRepository.save(customerConsent);
    }


    /**
     * Find consent record by ID.
     */
    @Override
    public Optional<CustomerConsent> findById(Long id) {

        return jpaCustomerConsentRepository.findById(id);
    }


    /**
     * Get complete consent history for a customer.
     * Latest records first.
     */
    @Override
    public List<CustomerConsent> findAllByCustomerId(
            Long customerId
    ) {

        return jpaCustomerConsentRepository
                .findAllByCustomerIdOrderByCreatedAtDesc(customerId);
    }


    /**
     * Get consent history for a customer
     * and specific consent type.
     */
    @Override
    public List<CustomerConsent> findAllByCustomerIdAndConsentType(
            Long customerId,
            ConsentType consentType
    ) {

        return jpaCustomerConsentRepository
                .findAllByCustomerIdAndConsentTypeOrderByCreatedAtDesc(
                        customerId,
                        consentType
                );
    }


    /**
     * Get latest consent record for a customer
     * and specific consent type.
     */
    @Override
    public Optional<CustomerConsent>
    findLatestByCustomerIdAndConsentType(
            Long customerId,
            ConsentType consentType
    ) {

        return jpaCustomerConsentRepository
                .findTopByCustomerIdAndConsentTypeOrderByCreatedAtDesc(
                        customerId,
                        consentType
                );
    }
}