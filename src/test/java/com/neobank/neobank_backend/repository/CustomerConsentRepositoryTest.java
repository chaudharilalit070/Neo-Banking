package com.neobank.neobank_backend.repository;

import com.neobank.neobank_backend.consent.domain.ConsentSource;
import com.neobank.neobank_backend.consent.domain.ConsentStatus;
import com.neobank.neobank_backend.consent.domain.ConsentType;
import com.neobank.neobank_backend.consent.domain.CustomerConsent;
import com.neobank.neobank_backend.consent.domain.CustomerConsentRepository;
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
class CustomerConsentRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerConsentRepository consentRepository;

    @Test
    @DisplayName("Should save, find history, and find latest consent by type")
    void testConsentRepository() {
        Customer customer = customerRepository.save(Customer.create(
                "CUS-CONS-001",
                CustomerType.INDIVIDUAL,
                "Frank",
                null,
                "Sinatra",
                LocalDate.of(1975, 12, 12),
                "USA",
                "test-user"
        ));

        CustomerConsent consent1 = CustomerConsent.builder()
                .customer(customer)
                .consentType(ConsentType.TERMS_AND_CONDITIONS)
                .status(ConsentStatus.GRANTED)
                .consentVersion("v1.0")
                .consentTextVersion("v1.0-text")
                .source(ConsentSource.WEB)
                .build();
        consentRepository.save(consent1);

        CustomerConsent consent2 = CustomerConsent.builder()
                .customer(customer)
                .consentType(ConsentType.TERMS_AND_CONDITIONS)
                .status(ConsentStatus.WITHDRAWN)
                .consentVersion("v1.0")
                .consentTextVersion("v1.0-text")
                .source(ConsentSource.MOBILE)
                .build();
        consentRepository.save(consent2);

        List<CustomerConsent> history = consentRepository.findAllByCustomerId(customer.getId());
        assertEquals(2, history.size());

        Optional<CustomerConsent> latest = consentRepository.findLatestByCustomerIdAndConsentType(
                customer.getId(), ConsentType.TERMS_AND_CONDITIONS);
        assertTrue(latest.isPresent());
        assertEquals(ConsentStatus.WITHDRAWN, latest.get().getStatus());
    }
}
