package com.neobank.neobank_backend.consent.infrastructure.persistence;

import com.neobank.neobank_backend.consent.domain.ConsentType;
import com.neobank.neobank_backend.consent.domain.CustomerConsent;
import com.neobank.neobank_backend.consent.domain.CustomerConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CustomerConsentRepositoryAdapter
        implements CustomerConsentRepository {

    private final JpaCustomerConsentRepository jpaCustomerConsentRepository;

    @Override
    public CustomerConsent save(CustomerConsent customerConsent) {
        return jpaCustomerConsentRepository.save(customerConsent);
    }

    @Override
    public Optional<CustomerConsent> findById(Long id) {
        return jpaCustomerConsentRepository.findById(id);
    }

    @Override
    public List<CustomerConsent> findAllByCustomerId(UUID customerId) {
        return jpaCustomerConsentRepository
                .findAllByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Override
    public List<CustomerConsent> findAllByCustomerIdAndConsentType(
            UUID customerId,
            ConsentType consentType
    ) {
        return jpaCustomerConsentRepository
                .findAllByCustomerIdAndConsentTypeOrderByCreatedAtDesc(
                        customerId,
                        consentType
                );
    }

    @Override
    public Optional<CustomerConsent> findLatestByCustomerIdAndConsentType(
            UUID customerId,
            ConsentType consentType
    ) {
        return jpaCustomerConsentRepository
                .findTopByCustomerIdAndConsentTypeOrderByCreatedAtDesc(
                        customerId,
                        consentType
                );
    }
}
