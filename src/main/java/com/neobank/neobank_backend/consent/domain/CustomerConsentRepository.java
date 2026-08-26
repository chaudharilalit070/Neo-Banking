package com.neobank.neobank_backend.consent.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerConsentRepository {

    CustomerConsent save(CustomerConsent customerConsent);

    Optional<CustomerConsent> findById(Long id);

    List<CustomerConsent> findAllByCustomerId(UUID customerId);

    List<CustomerConsent> findAllByCustomerIdAndConsentType(
            UUID customerId,
            ConsentType consentType
    );

    Optional<CustomerConsent> findLatestByCustomerIdAndConsentType(
            UUID customerId,
            ConsentType consentType
    );
}
