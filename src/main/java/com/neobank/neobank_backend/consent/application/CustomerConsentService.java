package com.neobank.neobank_backend.consent.application;

import com.neobank.neobank_backend.common.constants.ErrorCodes;
import com.neobank.neobank_backend.common.exception.ConflictException;
import com.neobank.neobank_backend.common.exception.ResourceNotFoundException;
import com.neobank.neobank_backend.consent.api.request.GrantCustomerConsentRequest;
import com.neobank.neobank_backend.consent.api.request.WithdrawCustomerConsentRequest;
import com.neobank.neobank_backend.consent.api.response.CustomerConsentResponse;
import com.neobank.neobank_backend.consent.domain.ConsentStatus;
import com.neobank.neobank_backend.consent.domain.ConsentType;
import com.neobank.neobank_backend.consent.domain.CustomerConsent;
import com.neobank.neobank_backend.consent.domain.CustomerConsentRepository;
import com.neobank.neobank_backend.customer.domain.Customer;
import com.neobank.neobank_backend.customer.domain.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerConsentService {

    private final CustomerRepository customerRepository;
    private final CustomerConsentRepository customerConsentRepository;

    @Transactional
    public CustomerConsentResponse grantConsent(
            UUID customerId,
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

    @Transactional
    public CustomerConsentResponse withdrawConsent(
            UUID customerId,
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
                                new ResourceNotFoundException(
                                        ErrorCodes.CONSENT_NOT_FOUND,
                                        "No existing consent found for customer"
                                )
                        );

        if (latestConsent.getStatus() == ConsentStatus.WITHDRAWN) {
            throw new ConflictException(
                    ErrorCodes.CONSENT_ALREADY_WITHDRAWN,
                    "Consent is already withdrawn"
            );
        }

        CustomerConsent withdrawalRecord =
                CustomerConsent.builder()
                        .customer(customer)
                        .consentType(latestConsent.getConsentType())
                        .status(ConsentStatus.WITHDRAWN)
                        .consentVersion(latestConsent.getConsentVersion())
                        .consentTextVersion(latestConsent.getConsentTextVersion())
                        .source(request.getSource())
                        .build();

        CustomerConsent savedConsent =
                customerConsentRepository.save(withdrawalRecord);

        return toResponse(savedConsent);
    }

    public List<CustomerConsentResponse> getConsentHistory(UUID customerId) {
        getCustomer(customerId);

        return customerConsentRepository
                .findAllByCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CustomerConsentResponse> getConsentHistoryByType(
            UUID customerId,
            ConsentType consentType
    ) {
        getCustomer(customerId);

        return customerConsentRepository
                .findAllByCustomerIdAndConsentType(customerId, consentType)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CustomerConsentResponse getLatestConsent(
            UUID customerId,
            ConsentType consentType
    ) {
        getCustomer(customerId);

        CustomerConsent consent =
                customerConsentRepository
                        .findLatestByCustomerIdAndConsentType(
                                customerId,
                                consentType
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        ErrorCodes.CONSENT_NOT_FOUND,
                                        "No consent record found for customer"
                                )
                        );

        return toResponse(consent);
    }

    private Customer getCustomer(UUID customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ErrorCodes.CUSTOMER_NOT_FOUND,
                                "Customer not found with id: " + customerId
                        )
                );
    }

    private CustomerConsentResponse toResponse(CustomerConsent consent) {
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
