package com.neobank.neobank_backend.consent.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaCustomerConsentRepository
        extends JpaRepository<CustomerConsent, Long> {


    /**
     * Get complete consent history for a customer.
     * Latest records first.
     */
    List<CustomerConsent> findAllByCustomerIdOrderByCreatedAtDesc(
            Long customerId
    );


    /**
     * Get complete consent history for a customer
     * and a specific consent type.
     * Latest records first.
     */
    List<CustomerConsent>
    findAllByCustomerIdAndConsentTypeOrderByCreatedAtDesc(
            Long customerId,
            ConsentType consentType
    );


    /**
     * Get the latest consent record for a customer
     * and a specific consent type.
     */
    Optional<CustomerConsent>
    findTopByCustomerIdAndConsentTypeOrderByCreatedAtDesc(
            Long customerId,
            ConsentType consentType
    );
}