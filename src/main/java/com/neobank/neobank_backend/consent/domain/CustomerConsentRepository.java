package com.neobank.neobank_backend.consent.domain;


import java.util.List;
import java.util.Optional;

public interface CustomerConsentRepository {

    /**
     * Save a new immutable consent history record.
     */
    CustomerConsent save(CustomerConsent customerConsent);


    /**
     * Find consent history record by ID.
     */
    Optional<CustomerConsent> findById(Long id);


    /**
     * Get complete consent history for a customer.
     * Latest records are returned first.
     */
    List<CustomerConsent> findAllByCustomerId(Long customerId);


    /**
     * Get consent history for a customer and specific consent type.
     */
    List<CustomerConsent> findAllByCustomerIdAndConsentType(
            Long customerId,
            ConsentType consentType
    );


    /**
     * Get the latest consent record for a customer and consent type.
     */
    Optional<CustomerConsent> findLatestByCustomerIdAndConsentType(
            Long customerId,
            ConsentType consentType
    );
}