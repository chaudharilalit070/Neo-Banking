package com.neobank.neobank_backend.consent.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerConsentService {

    private final CustomerRepository customerRepository;
    private final CustomerConsentRepository customerConsentRepository;


    /**
     * Grant customer consent.
     */
    @Transactional
    public CustomerConsentResponse grantConsent(
            Long customerId,
            GrantCustomerConsentRequest request
    ) {

        Customer customer = getCustomer(customerId);

        CustomerConsent consent = CustomerConsent.builder()
                .customer(customer)
                .consentType(request.getConsentType())
                .status(ConsentStatus.GRANTED)
                .consentVersion(request.getConsentVersion())
                .consentTextVersion(request.getConsentTextVersion())
                .source(request.getSource())
                .build();

        CustomerConsent savedConsent =
                customerConsentRepository.save(consent);

        return toResponse(savedConsent);
    }


    /**
     * Withdraw customer consent.
     */
    @Transactional
    public CustomerConsentResponse withdrawConsent(
            Long customerId,
            WithdrawCustomerConsentRequest request
    ) {

        Customer customer = getCustomer(customerId);

        CustomerConsent latestConsent =
                customerConsentRepository
                        .findLatestByCustomerIdAndConsentType(
                                customerId,
                                request.getConsentType()
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No existing consent found for customer"
                                )
                        );

        if (latestConsent.getStatus() == ConsentStatus.WITHDRAWN) {
            throw new IllegalStateException(
                    "Consent is already withdrawn"
            );
        }

        CustomerConsent withdrawalRecord =
                CustomerConsent.builder()
                        .customer(customer)
                        .consentType(latestConsent.getConsentType())
                        .status(ConsentStatus.WITHDRAWN)
                        .consentVersion(latestConsent.getConsentVersion())
                        .consentTextVersion(
                                latestConsent.getConsentTextVersion()
                        )
                        .source(request.getSource())
                        .build();

        CustomerConsent savedConsent =
                customerConsentRepository.save(withdrawalRecord);

        return toResponse(savedConsent);
    }


    /**
     * Get complete consent history for a customer.
     */
    public List<CustomerConsentResponse> getConsentHistory(
            Long customerId
    ) {

        getCustomer(customerId);

        return customerConsentRepository
                .findAllByCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    /**
     * Get consent history for a specific consent type.
     */
    public List<CustomerConsentResponse> getConsentHistoryByType(
            Long customerId,
            com.company.neobanking.customer.customer.domain.consent.ConsentType consentType
    ) {

        getCustomer(customerId);

        return customerConsentRepository
                .findAllByCustomerIdAndConsentType(
                        customerId,
                        consentType
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }


    /**
     * Get latest/current consent for a specific consent type.
     */
    public CustomerConsentResponse getLatestConsent(
            Long customerId,
            com.company.neobanking.customer.customer.domain.consent.ConsentType consentType
    ) {

        getCustomer(customerId);

        CustomerConsent consent =
                customerConsentRepository
                        .findLatestByCustomerIdAndConsentType(
                                customerId,
                                consentType
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No consent record found for customer"
                                )
                        );

        return toResponse(consent);
    }


    /**
     * Find customer.
     */
    private Customer getCustomer(Long customerId) {

        return customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Customer not found with id: " + customerId
                        )
                );
    }


    /**
     * Convert domain entity to API response.
     */
    private CustomerConsentResponse toResponse(
            CustomerConsent consent
    ) {

        return CustomerConsentResponse.builder()
                .id(consent.getId())
                .customerId(consent.getCustomer().getId())
                .consentType(consent.getConsentType())
                .status(consent.getStatus())
                .consentVersion(consent.getConsentVersion())
                .consentTextVersion(consent.getConsentTextVersion())
                .source(consent.getSource())
                .grantedAt(consent.getGrantedAt())
                .withdrawnAt(consent.getWithdrawnAt())
                .createdAt(consent.getCreatedAt())
                .build();
    }
}