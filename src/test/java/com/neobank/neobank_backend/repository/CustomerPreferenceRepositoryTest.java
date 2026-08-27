package com.neobank.neobank_backend.repository;

import com.neobank.neobank_backend.customer.domain.Customer;
import com.neobank.neobank_backend.customer.domain.CustomerRepository;
import com.neobank.neobank_backend.customer.domain.CustomerType;
import com.neobank.neobank_backend.preference.domain.CommunicationChannel;
import com.neobank.neobank_backend.preference.domain.CustomerPreference;
import com.neobank.neobank_backend.preference.domain.CustomerPreferenceRepository;
import com.neobank.neobank_backend.preference.domain.PreferenceStatus;
import com.neobank.neobank_backend.preference.domain.PreferredLanguage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CustomerPreferenceRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerPreferenceRepository preferenceRepository;

    @Test
    @DisplayName("Should save, find, and verify preferences by customerId")
    void testPreferenceRepository() {
        Customer customer = customerRepository.save(Customer.create(
                "CUS-PREF-001",
                CustomerType.INDIVIDUAL,
                "Emma",
                null,
                "Watson",
                LocalDate.of(1990, 4, 15),
                "GBR",
                "test-user"
        ));

        CustomerPreference preference = CustomerPreference.builder()
                .customer(customer)
                .preferredLanguage(PreferredLanguage.EN)
                .preferredCommunicationChannel(CommunicationChannel.EMAIL)
                .marketingNotifications(false)
                .transactionNotifications(true)
                .securityNotifications(true)
                .status(PreferenceStatus.ACTIVE)
                .build();
        preferenceRepository.save(preference);

        assertTrue(preferenceRepository.existsByCustomerId(customer.getId()));

        Optional<CustomerPreference> found = preferenceRepository.findByCustomerId(customer.getId());
        assertTrue(found.isPresent());
        assertEquals(PreferredLanguage.EN, found.get().getPreferredLanguage());
        assertEquals(CommunicationChannel.EMAIL, found.get().getPreferredCommunicationChannel());
    }
}
