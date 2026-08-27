package com.neobank.neobank_backend.customer.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DefaultCustomerNumberGeneratorTest {

    private final DefaultCustomerNumberGenerator generator = new DefaultCustomerNumberGenerator();

    @Test
    @DisplayName("Should generate customer number with CUS- prefix")
    void testGenerateFormat() {
        String customerNumber = generator.generate();
        assertNotNull(customerNumber);
        assertTrue(customerNumber.startsWith("CUS-"));
        assertEquals(20, customerNumber.length()); // "CUS-" (4) + 16 chars = 20
    }

    @Test
    @DisplayName("Should generate unique customer numbers")
    void testGenerateUniqueness() {
        Set<String> generated = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String num = generator.generate();
            assertTrue(generated.add(num), "Duplicate customer number found: " + num);
        }
    }
}
