package com.neobank.neobank_backend.repository;

import com.neobank.neobank_backend.customer.domain.Customer;
import com.neobank.neobank_backend.customer.domain.CustomerRepository;
import com.neobank.neobank_backend.customer.domain.CustomerType;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycle;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycleReason;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycleRepository;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycleStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CustomerLifecycleRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerLifecycleRepository lifecycleRepository;

    @Test
    @DisplayName("Should save lifecycle transitions and fetch latest and history")
    void testLifecycleRepository() {
        Customer customer = customerRepository.save(Customer.create(
                "CUS-LC-001",
                CustomerType.INDIVIDUAL,
                "Bob",
                null,
                "Builder",
                LocalDate.of(1988, 3, 15),
                "CAN",
                "test-user"
        ));

        CustomerLifecycle stage1 = CustomerLifecycle.builder()
                .customer(customer)
                .previousStatus(null)
                .currentStatus(CustomerLifecycleStatus.PROSPECT)
                .reason(CustomerLifecycleReason.INITIAL)
                .effectiveAt(LocalDateTime.now().minusHours(2))
                .build();
        lifecycleRepository.save(stage1);

        CustomerLifecycle stage2 = CustomerLifecycle.builder()
                .customer(customer)
                .previousStatus(CustomerLifecycleStatus.PROSPECT)
                .currentStatus(CustomerLifecycleStatus.ONBOARDING)
                .reason(CustomerLifecycleReason.ONBOARDING_STARTED)
                .effectiveAt(LocalDateTime.now().minusHours(1))
                .build();
        lifecycleRepository.save(stage2);

        Optional<CustomerLifecycle> latest = lifecycleRepository.findLatestByCustomerId(customer.getId());
        assertTrue(latest.isPresent());
        assertEquals(CustomerLifecycleStatus.ONBOARDING, latest.get().getCurrentStatus());

        List<CustomerLifecycle> history = lifecycleRepository.findAllByCustomerId(customer.getId());
        assertEquals(2, history.size());
    }
}
