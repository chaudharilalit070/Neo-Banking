package com.neobank.neobank_backend.repository;

import com.neobank.neobank_backend.contact.domain.contact.ContactStatus;
import com.neobank.neobank_backend.contact.domain.contact.ContactType;
import com.neobank.neobank_backend.contact.domain.contact.ContactVerificationStatus;
import com.neobank.neobank_backend.contact.domain.contact.CustomerContact;
import com.neobank.neobank_backend.contact.domain.contact.CustomerContactRepository;
import com.neobank.neobank_backend.customer.domain.Customer;
import com.neobank.neobank_backend.customer.domain.CustomerRepository;
import com.neobank.neobank_backend.customer.domain.CustomerType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CustomerContactRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerContactRepository contactRepository;

    @Test
    @DisplayName("Should save, query by customer, and check uniqueness & primary status")
    void testContactRepository() {
        Customer customer = customerRepository.save(Customer.create(
                "CUS-CONT-001",
                CustomerType.INDIVIDUAL,
                "Charlie",
                null,
                "Brown",
                LocalDate.of(1992, 7, 10),
                "USA",
                "test-user"
        ));

        CustomerContact emailContact = CustomerContact.builder()
                .customer(customer)
                .contactType(ContactType.EMAIL)
                .contactValue("charlie@peanuts.com")
                .primary(true)
                .verificationStatus(ContactVerificationStatus.PENDING)
                .status(ContactStatus.ACTIVE)
                .build();
        contactRepository.save(emailContact);

        CustomerContact phoneContact = CustomerContact.builder()
                .customer(customer)
                .contactType(ContactType.PHONE)
                .contactValue("+15551234567")
                .primary(true)
                .verificationStatus(ContactVerificationStatus.VERIFIED)
                .status(ContactStatus.ACTIVE)
                .build();
        contactRepository.save(phoneContact);

        List<CustomerContact> contacts = contactRepository.findByCustomerId(customer.getId());
        assertEquals(2, contacts.size());

        assertTrue(contactRepository.existsByCustomerIdAndContactTypeAndContactValue(
                customer.getId(), ContactType.EMAIL, "charlie@peanuts.com"));

        Optional<CustomerContact> primaryEmail = contactRepository.findByCustomerIdAndContactTypeAndPrimaryTrue(
                customer.getId(), ContactType.EMAIL);
        assertTrue(primaryEmail.isPresent());
        assertEquals("charlie@peanuts.com", primaryEmail.get().getContactValue());
    }
}
