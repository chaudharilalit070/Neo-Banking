package com.neobank.neobank_backend.preference.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerPreferenceService {

    private final CustomerRepository customerRepository;
    private final CustomerPreferenceRepository customerPreferenceRepository;


    /**
     * Create preference profile for a customer.
     */
    public CustomerPreferenceResponse createPreferences(
            Long customerId,
            CreateCustomerPreferenceRequest request
    ) {

        // 1. Check customer exists
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ErrorCodes.CUSTOMER_NOT_FOUND,
                                "Customer not found with id: " + customerId
                        )
                );

        // 2. One preference profile per customer
        if (customerPreferenceRepository.existsByCustomerId(customerId)) {
            throw new ConflictException(
                    ErrorCodes.CUSTOMER_PREFERENCE_ALREADY_EXISTS,
                    "Customer preferences already exist for customer id: "
                            + customerId
            );
        }

        // 3. Create preference profile
        CustomerPreference preference =
                CustomerPreference.builder()
                        .customer(customer)
                        .preferredLanguage(
                                request.getPreferredLanguage()
                        )
                        .preferredCommunicationChannel(
                                request.getPreferredCommunicationChannel()
                        )
                        .marketingNotifications(
                                request.getMarketingNotifications()
                        )
                        .transactionNotifications(
                                request.getTransactionNotifications()
                        )
                        .securityNotifications(
                                request.getSecurityNotifications()
                        )
                        .status(PreferenceStatus.ACTIVE)
                        .build();

        // 4. Save
        CustomerPreference savedPreference =
                customerPreferenceRepository.save(preference);

        // 5. Return response
        return mapToResponse(savedPreference);
    }


    /**
     * Get customer preferences.
     */
    @Transactional(readOnly = true)
    public CustomerPreferenceResponse getPreferences(
            Long customerId
    ) {

        // Check customer exists
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
                                        ErrorCodes.CUSTOMER_PREFERENCE_NOT_FOUND,
                                        "Customer preferences not found for customer id: "
                                                + customerId
                                )
                        );

        return mapToResponse(preference);
    }


    /**
     * Partially update customer preferences.
     */
    public CustomerPreferenceResponse updatePreferences(
            Long customerId,
            UpdateCustomerPreferenceRequest request
    ) {

        // Check customer exists
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    ErrorCodes.CUSTOMER_NOT_FOUND,
                    "Customer not found with id: " + customerId
            );
        }

        // Get existing preferences
        CustomerPreference preference =
                customerPreferenceRepository
                        .findByCustomerId(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        ErrorCodes.CUSTOMER_PREFERENCE_NOT_FOUND,
                                        "Customer preferences not found for customer id: "
                                                + customerId
                                )
                        );

        /*
         * Update only fields provided in the request.
         */

        if (request.getPreferredLanguage() != null) {
            preference.setPreferredLanguage(
                    request.getPreferredLanguage()
            );
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


    /**
     * Deactivate customer preferences.
     */
    public CustomerPreferenceResponse deactivatePreferences(
            Long customerId
    ) {

        // Check customer exists
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
                                        ErrorCodes.CUSTOMER_PREFERENCE_NOT_FOUND,
                                        "Customer preferences not found for customer id: "
                                                + customerId
                                )
                        );

        preference.setStatus(PreferenceStatus.INACTIVE);

        CustomerPreference updatedPreference =
                customerPreferenceRepository.save(preference);

        return mapToResponse(updatedPreference);
    }


    /**
     * Convert entity to API response.
     */
    private CustomerPreferenceResponse mapToResponse(
            CustomerPreference preference
    ) {

        return CustomerPreferenceResponse.builder()
                .id(preference.getId())
                .customerId(preference.getCustomer().getId())
                .preferredLanguage(
                        preference.getPreferredLanguage()
                )
                .preferredCommunicationChannel(
                        preference.getPreferredCommunicationChannel()
                )
                .marketingNotifications(
                        preference.getMarketingNotifications()
                )
                .transactionNotifications(
                        preference.getTransactionNotifications()
                )
                .securityNotifications(
                        preference.getSecurityNotifications()
                )
                .status(preference.getStatus())
                .createdAt(preference.getCreatedAt())
                .updatedAt(preference.getUpdatedAt())
                .build();
    }
}