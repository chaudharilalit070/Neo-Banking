package com.neobank.neobank_backend.repository;

import com.neobank.neobank_backend.customer.domain.Customer;
import com.neobank.neobank_backend.customer.domain.CustomerRepository;
import com.neobank.neobank_backend.customer.domain.CustomerStatus;
import com.neobank.neobank_backend.customer.domain.CustomerType;
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
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("Should save, findById, and verify existence by customer number")
    void testCustomerRepositoryOperations() {
        Customer customer = Customer.create(
                "CUS-TEST-0001",
                CustomerType.INDIVIDUAL,
                "Alice",
                "M",
                "Wonderland",
                LocalDate.of(1995, 5, 20),
                "USA",
                "system-test"
        );

        Customer saved = customerRepository.save(customer);
        assertNotNull(saved.getId());

        Optional<Customer> foundById = customerRepository.findById(saved.getId());
        assertTrue(foundById.isPresent());
        assertEquals("Alice", foundById.get().getFirstName());
        assertEquals(CustomerStatus.PROSPECT, foundById.get().getCustomerStatus());

        assertTrue(customerRepository.existsByCustomerNumber("CUS-TEST-0001"));
        assertFalse(customerRepository.existsByCustomerNumber("CUS-NONEXISTENT"));
        assertTrue(customerRepository.existsById(saved.getId()));
    }
}
