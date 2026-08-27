package com.neobank.neobank_backend.customer.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    @DisplayName("Should create customer with initial PROSPECT status and version 0")
    void testCreateCustomer() {
        Customer customer = Customer.create(
                "CUS-1234567890ABCDEF",
                CustomerType.INDIVIDUAL,
                "John",
                "David",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "USA",
                "admin-user"
        );

        assertNotNull(customer.getId());
        assertEquals("CUS-1234567890ABCDEF", customer.getCustomerNumber());
        assertEquals(CustomerType.INDIVIDUAL, customer.getCustomerType());
        assertEquals(CustomerStatus.PROSPECT, customer.getCustomerStatus());
        assertEquals("John", customer.getFirstName());
        assertEquals("David", customer.getMiddleName());
        assertEquals("Doe", customer.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), customer.getDateOfBirth());
        assertEquals("USA", customer.getNationality());
        assertEquals("admin-user", customer.getCreatedBy());
        assertNotNull(customer.getCreatedAt());
        assertEquals(0L, customer.getVersion());
    }

    @Test
    @DisplayName("Should update customer profile correctly")
    void testUpdateCustomerProfile() {
        Customer customer = Customer.create(
                "CUS-1234567890ABCDEF",
                CustomerType.INDIVIDUAL,
                "John",
                null,
                "Doe",
                LocalDate.of(1990, 1, 1),
                "USA",
                "admin-user"
        );

        customer.updateProfile(
                "Johnny",
                "David",
                "Doe-Smith",
                LocalDate.of(1990, 1, 2),
                "GBR",
                "editor-user"
        );

        assertEquals("Johnny", customer.getFirstName());
        assertEquals("David", customer.getMiddleName());
        assertEquals("Doe-Smith", customer.getLastName());
        assertEquals(LocalDate.of(1990, 1, 2), customer.getDateOfBirth());
        assertEquals("GBR", customer.getNationality());
        assertEquals("editor-user", customer.getUpdatedBy());
        assertNotNull(customer.getUpdatedAt());
    }

    @Test
    @DisplayName("Should update customer status")
    void testUpdateCustomerStatus() {
        Customer customer = Customer.create(
                "CUS-1234567890ABCDEF",
                CustomerType.INDIVIDUAL,
                "John",
                null,
                "Doe",
                LocalDate.of(1990, 1, 1),
                "USA",
                "admin-user"
        );

        customer.updateStatus(CustomerStatus.ACTIVE, "ops-user");
        assertEquals(CustomerStatus.ACTIVE, customer.getCustomerStatus());
        assertEquals("ops-user", customer.getUpdatedBy());
        assertNotNull(customer.getUpdatedAt());
    }
}
