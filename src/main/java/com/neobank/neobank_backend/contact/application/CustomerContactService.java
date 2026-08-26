package com.neobank.neobank_backend.contact.application;

import com.neobank.neobank_backend.common.constants.ErrorCodes;
import com.neobank.neobank_backend.common.exception.ConflictException;
import com.neobank.neobank_backend.common.exception.ResourceNotFoundException;
import com.neobank.neobank_backend.contact.api.request.contact.AddCustomerContactRequest;
import com.neobank.neobank_backend.contact.api.response.contact.CustomerContactResponse;
import com.neobank.neobank_backend.contact.domain.contact.ContactStatus;
import com.neobank.neobank_backend.contact.domain.contact.ContactType;
import com.neobank.neobank_backend.contact.domain.contact.ContactVerificationStatus;
import com.neobank.neobank_backend.contact.domain.contact.CustomerContact;
import com.neobank.neobank_backend.contact.domain.contact.CustomerContactRepository;
import com.neobank.neobank_backend.customer.domain.Customer;
import com.neobank.neobank_backend.customer.domain.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerContactService {

    private final CustomerRepository customerRepository;
    private final CustomerContactRepository customerContactRepository;

    public CustomerContactResponse addContact(
            UUID customerId,
            AddCustomerContactRequest request
    ) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ErrorCodes.CUSTOMER_NOT_FOUND,
                                "Customer not found with id: " + customerId
                        )
                );

        boolean contactExists =
                customerContactRepository
                        .existsByCustomerIdAndContactTypeAndContactValue(
                                customerId,
                                request.getContactType(),
                                request.getContactValue()
                        );

        if (contactExists) {
            throw new ConflictException(
                    ErrorCodes.CONTACT_ALREADY_EXISTS,
                    "Contact already exists for this customer"
            );
        }

        if (request.isPrimary()) {
            customerContactRepository
                    .findByCustomerIdAndContactTypeAndPrimaryTrue(
                            customerId,
                            request.getContactType()
                    )
                    .ifPresent(existingPrimary -> {
                        existingPrimary.setPrimary(false);
                        customerContactRepository.save(existingPrimary);
                    });
        }

        CustomerContact customerContact =
                CustomerContact.builder()
                        .customer(customer)
                        .contactType(request.getContactType())
                        .contactValue(
                                normalizeContactValue(
                                        request.getContactType(),
                                        request.getContactValue()
                                )
                        )
                        .primary(request.isPrimary())
                        .verificationStatus(ContactVerificationStatus.PENDING)
                        .status(ContactStatus.ACTIVE)
                        .build();

        CustomerContact savedContact =
                customerContactRepository.save(customerContact);

        return mapToResponse(savedContact);
    }

    @Transactional(readOnly = true)
    public List<CustomerContactResponse> getCustomerContacts(UUID customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    ErrorCodes.CUSTOMER_NOT_FOUND,
                    "Customer not found with id: " + customerId
            );
        }

        return customerContactRepository
                .findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CustomerContactResponse setPrimaryContact(
            UUID customerId,
            Long contactId
    ) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    ErrorCodes.CUSTOMER_NOT_FOUND,
                    "Customer not found with id: " + customerId
            );
        }

        CustomerContact customerContact =
                customerContactRepository.findById(contactId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        ErrorCodes.CONTACT_NOT_FOUND,
                                        "Customer contact not found with id: "
                                                + contactId
                                )
                        );

        if (!customerContact.getCustomer().getId().equals(customerId)) {
            throw new ConflictException(
                    ErrorCodes.CONTACT_DOES_NOT_BELONG_TO_CUSTOMER,
                    "Contact does not belong to this customer"
            );
        }

        customerContactRepository
                .findByCustomerIdAndContactTypeAndPrimaryTrue(
                        customerId,
                        customerContact.getContactType()
                )
                .ifPresent(existingPrimary -> {
                    if (!existingPrimary.getId().equals(customerContact.getId())) {
                        existingPrimary.setPrimary(false);
                        customerContactRepository.save(existingPrimary);
                    }
                });

        customerContact.setPrimary(true);

        CustomerContact updatedContact =
                customerContactRepository.save(customerContact);

        return mapToResponse(updatedContact);
    }

    public CustomerContactResponse verifyContact(
            UUID customerId,
            Long contactId
    ) {
        CustomerContact customerContact =
                getCustomerContactForCustomer(customerId, contactId);

        customerContact.setVerificationStatus(ContactVerificationStatus.VERIFIED);
        customerContact.setVerifiedAt(java.time.LocalDateTime.now());

        CustomerContact updatedContact =
                customerContactRepository.save(customerContact);

        return mapToResponse(updatedContact);
    }

    public CustomerContactResponse deactivateContact(
            UUID customerId,
            Long contactId
    ) {
        CustomerContact customerContact =
                getCustomerContactForCustomer(customerId, contactId);

        customerContact.setStatus(ContactStatus.INACTIVE);
        customerContact.setPrimary(false);

        CustomerContact updatedContact =
                customerContactRepository.save(customerContact);

        return mapToResponse(updatedContact);
    }

    private CustomerContact getCustomerContactForCustomer(
            UUID customerId,
            Long contactId
    ) {
        CustomerContact customerContact =
                customerContactRepository.findById(contactId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        ErrorCodes.CONTACT_NOT_FOUND,
                                        "Customer contact not found with id: "
                                                + contactId
                                )
                        );

        if (!customerContact.getCustomer().getId().equals(customerId)) {
            throw new ConflictException(
                    ErrorCodes.CONTACT_DOES_NOT_BELONG_TO_CUSTOMER,
                    "Contact does not belong to this customer"
            );
        }

        return customerContact;
    }

    private String normalizeContactValue(
            ContactType contactType,
            String contactValue
    ) {
        if (contactValue == null || contactValue.isBlank()) {
            return null;
        }

        String normalizedValue = contactValue.trim();

        if (contactType == ContactType.EMAIL) {
            return normalizedValue.toLowerCase();
        }

        return normalizedValue;
    }

    private CustomerContactResponse mapToResponse(CustomerContact contact) {
        return CustomerContactResponse.builder()
                .id(contact.getId())
                .customerId(contact.getCustomer().getId())
                .contactType(contact.getContactType())
                .contactValue(contact.getContactValue())
                .primary(contact.isPrimary())
                .verificationStatus(contact.getVerificationStatus())
                .status(contact.getStatus())
                .verifiedAt(contact.getVerifiedAt())
                .createdAt(contact.getCreatedAt())
                .updatedAt(contact.getUpdatedAt())
                .build();
    }
}
