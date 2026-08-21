package com.neobank.neobank_backend.contact.application;
import com.neobank.neobank_backend.contact.api.request.contact.AddCustomerContactRequest;
import com.neobank.neobank_backend.contact.api.response.contact.CustomerContactResponse;
import com.neobank.neobank_backend.contact.domain.contact.ContactStatus;
import com.neobank.neobank_backend.contact.domain.contact.ContactVerificationStatus;
import com.neobank.neobank_backend.contact.domain.contact.CustomerContact;
import com.neobank.neobank_backend.customer.domain.Customer;
import com.neobank.neobank_backend.customer.domain.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerContactService {

    private final CustomerRepository customerRepository;
    private final CustomerContactRepository customerContactRepository;


    public CustomerContactResponse addContact(
            Long customerId,
            AddCustomerContactRequest request
    ) {

        // 1. Check customer exists
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer not found with id: " + customerId
                        )
                );

        // 2. Check duplicate contact
        boolean contactExists =
                customerContactRepository
                        .existsByCustomerIdAndContactTypeAndContactValue(
                                customerId,
                                request.getContactType(),
                                request.getContactValue()
                        );

        if (contactExists) {
            throw new RuntimeException(
                    "Contact already exists for this customer"
            );
        }

        // 3. Check primary contact rule
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

        // 4. Create contact
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
                        .verificationStatus(
                                ContactVerificationStatus.PENDING
                        )
                        .status(ContactStatus.ACTIVE)
                        .build();

        // 5. Save
        CustomerContact savedContact =
                customerContactRepository.save(customerContact);

        // 6. Return response
        return mapToResponse(savedContact);
    }


    @Transactional(readOnly = true)
    public List<CustomerContactResponse> getCustomerContacts(
            Long customerId
    ) {

        // Check customer exists
        if (!customerRepository.existsById(customerId)) {
            throw new RuntimeException(
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
            Long customerId,
            Long contactId
    ) {

        // 1. Check customer exists
        if (!customerRepository.existsById(customerId)) {
            throw new RuntimeException(
                    "Customer not found with id: " + customerId
            );
        }

        // 2. Get requested contact
        CustomerContact customerContact =
                customerContactRepository.findById(contactId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customer contact not found with id: "
                                                + contactId
                                )
                        );

        // Security/business validation:
        // Contact must belong to the requested customer
        if (!customerContact.getCustomer()
                .getId()
                .equals(customerId)) {

            throw new RuntimeException(
                    "Contact does not belong to this customer"
            );
        }

        // 3. Remove primary from existing contact
        customerContactRepository
                .findByCustomerIdAndContactTypeAndPrimaryTrue(
                        customerId,
                        customerContact.getContactType()
                )
                .ifPresent(existingPrimary -> {

                    if (!existingPrimary.getId()
                            .equals(customerContact.getId())) {

                        existingPrimary.setPrimary(false);
                        customerContactRepository.save(existingPrimary);
                    }
                });

        // 4. Make selected contact primary
        customerContact.setPrimary(true);

        CustomerContact updatedContact =
                customerContactRepository.save(customerContact);

        return mapToResponse(updatedContact);
    }


    public CustomerContactResponse verifyContact(
            Long customerId,
            Long contactId
    ) {

        CustomerContact customerContact =
                getCustomerContactForCustomer(
                        customerId,
                        contactId
                );

        customerContact.setVerificationStatus(
                ContactVerificationStatus.VERIFIED
        );

        customerContact.setVerifiedAt(
                java.time.LocalDateTime.now()
        );

        CustomerContact updatedContact =
                customerContactRepository.save(customerContact);

        return mapToResponse(updatedContact);
    }


    public CustomerContactResponse deactivateContact(
            Long customerId,
            Long contactId
    ) {

        CustomerContact customerContact =
                getCustomerContactForCustomer(
                        customerId,
                        contactId
                );

        customerContact.setStatus(ContactStatus.INACTIVE);

        customerContact.setPrimary(false);

        CustomerContact updatedContact =
                customerContactRepository.save(customerContact);

        return mapToResponse(updatedContact);
    }


    private CustomerContact getCustomerContactForCustomer(
            Long customerId,
            Long contactId
    ) {

        CustomerContact customerContact =
                customerContactRepository.findById(contactId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customer contact not found with id: "
                                                + contactId
                                )
                        );

        if (!customerContact.getCustomer()
                .getId()
                .equals(customerId)) {

            throw new RuntimeException(
                    "Contact does not belong to this customer"
            );
        }

        return customerContact;
    }


    private String normalizeContactValue(
            ContactType contactType,
            String contactValue
    ) {

        if (contactValue == null) {
            return null;
        }

        String normalizedValue = contactValue.trim();

        if (contactType == ContactType.EMAIL) {
            return normalizedValue.toLowerCase();
        }

        return normalizedValue;
    }


    private CustomerContactResponse mapToResponse(
            CustomerContact contact
    ) {

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
