package com.neobank.neobank_backend.consent.infrastructure.persistence;

import com.neobank.neobank_backend.consent.domain.ConsentType;
import com.neobank.neobank_backend.consent.domain.CustomerConsent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaCustomerConsentRepository
        extends JpaRepository<CustomerConsent, Long> {

    List<CustomerConsent> findAllByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    List<CustomerConsent> findAllByCustomerIdAndConsentTypeOrderByCreatedAtDesc(
            UUID customerId,
            ConsentType consentType
    );

    Optional<CustomerConsent> findTopByCustomerIdAndConsentTypeOrderByCreatedAtDesc(
            UUID customerId,
            ConsentType consentType
    );
}
