package com.neobank.neobank_backend.preference.application;

import com.neobank.neobank_backend.common.constants.ErrorCodes;
import com.neobank.neobank_backend.common.exception.ConflictException;
import com.neobank.neobank_backend.common.exception.ResourceNotFoundException;
import com.neobank.neobank_backend.customer.domain.Customer;
import com.neobank.neobank_backend.customer.domain.CustomerRepository;
import com.neobank.neobank_backend.preference.api.request.CreateCustomerPreferenceRequest;
import com.neobank.neobank_backend.preference.api.request.UpdateCustomerPreferenceRequest;
import com.neobank.neobank_backend.preference.api.respons.CustomerPreferenceResponse;
import com.neobank.neobank_backend.preference.domain.CustomerPreference;
import com.neobank.neobank_backend.preference.domain.CustomerPreferenceRepository;
import com.neobank.neobank_backend.preference.domain.PreferenceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerPreferenceService {

    private final CustomerRepository customerRepository;
    private final CustomerPreferenceRepository customerPreferenceRepository;

    public CustomerPreferenceResponse createPreferences(
            UUID customerId,
            CreateCustomerPreferenceRequest request
    ) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ErrorCodes.CUSTOMER_NOT_FOUND,
                                "Customer not found with id: " + customerId
                        )
                );

        if (customerPreferenceRepository.existsByCustomerId(customerId)) {
            throw new ConflictException(
                    ErrorCodes.PREFERENCE_ALREADY_EXISTS,
                    "Customer preferences already exist for customer id: "
                            + customerId
            );
        }

        CustomerPreference preference =
                CustomerPreference.builder()
                        .customer(customer)
                        .preferredLanguage(request.getPreferredLanguage())
                        .preferredCommunicationChannel(
                                request.getPreferredCommunicationChannel()
                        )
                        .marketingNotifications(request.getMarketingNotifications())
                        .transactionNotifications(
                                request.getTransactionNotifications()
                        )
                        .securityNotifications(request.getSecurityNotifications())
                        .status(PreferenceStatus.ACTIVE)
                        .build();

        CustomerPreference savedPreference =
                customerPreferenceRepository.save(preference);

        return mapToResponse(savedPreference);
    }

    @Transactional(readOnly = true)
    public CustomerPreferenceResponse getPreferences(UUID customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    ErrorCodes.CUSTOMER_NOT_FOUND,
                    "Customer not found with id: " + customerId
            );
        }

        CustomerPreference preference =
                customerPreferenceRepository
                        .findByCustomerId(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        ErrorCodes.PREFERENCE_NOT_FOUND,
                                        "Customer preferences not found for customer id: "
                                                + customerId
                                )
                        );

        return mapToResponse(preference);
    }

    public CustomerPreferenceResponse updatePreferences(
            UUID customerId,
            UpdateCustomerPreferenceRequest request
    ) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    ErrorCodes.CUSTOMER_NOT_FOUND,
                    "Customer not found with id: " + customerId
            );
        }

        CustomerPreference preference =
                customerPreferenceRepository
                        .findByCustomerId(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        ErrorCodes.PREFERENCE_NOT_FOUND,
                                        "Customer preferences not found for customer id: "
                                                + customerId
                                )
                        );

        if (request.getPreferredLanguage() != null) {
            preference.setPreferredLanguage(request.getPreferredLanguage());
        }

        if (request.getPreferredCommunicationChannel() != null) {
            preference.setPreferredCommunicationChannel(
                    request.getPreferredCommunicationChannel()
            );
        }

        if (request.getMarketingNotifications() != null) {
            preference.setMarketingNotifications(
                    request.getMarketingNotifications()
            );
        }

        if (request.getTransactionNotifications() != null) {
            preference.setTransactionNotifications(
                    request.getTransactionNotifications()
            );
        }

        if (request.getSecurityNotifications() != null) {
            preference.setSecurityNotifications(
                    request.getSecurityNotifications()
            );
        }

        CustomerPreference updatedPreference =
                customerPreferenceRepository.save(preference);

        return mapToResponse(updatedPreference);
    }

    public CustomerPreferenceResponse deactivatePreferences(UUID customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    ErrorCodes.CUSTOMER_NOT_FOUND,
                    "Customer not found with id: " + customerId
            );
        }

        CustomerPreference preference =
                customerPreferenceRepository
                        .findByCustomerId(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        ErrorCodes.PREFERENCE_NOT_FOUND,
                                        "Customer preferences not found for customer id: "
                                                + customerId
                                )
                        );

        preference.setStatus(PreferenceStatus.INACTIVE);

        CustomerPreference updatedPreference =
                customerPreferenceRepository.save(preference);

        return mapToResponse(updatedPreference);
    }

    private CustomerPreferenceResponse mapToResponse(
            CustomerPreference preference
    ) {
        return CustomerPreferenceResponse.builder()
                .id(preference.getId())
                .customerId(preference.getCustomer().getId())
                .preferredLanguage(preference.getPreferredLanguage())
                .preferredCommunicationChannel(
                        preference.getPreferredCommunicationChannel()
                )
                .marketingNotifications(preference.getMarketingNotifications())
                .transactionNotifications(
                        preference.getTransactionNotifications()
                )
                .securityNotifications(preference.getSecurityNotifications())
                .status(preference.getStatus())
                .createdAt(preference.getCreatedAt())
                .updatedAt(preference.getUpdatedAt())
                .build();
    }
}
