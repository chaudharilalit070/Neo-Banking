package com.neobank.neobank_backend.repository;

import com.neobank.neobank_backend.address.domain.AddressStatus;
import com.neobank.neobank_backend.address.domain.AddressType;
import com.neobank.neobank_backend.address.domain.CustomerAddress;
import com.neobank.neobank_backend.address.domain.CustomerAddressRepository;
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
class CustomerAddressRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerAddressRepository addressRepository;

    @Test
    @DisplayName("Should save, query, and verify address uniqueness by customer and address type")
    void testAddressRepository() {
        Customer customer = customerRepository.save(Customer.create(
                "CUS-ADDR-001",
                CustomerType.INDIVIDUAL,
                "David",
                null,
                "Copperfield",
                LocalDate.of(1985, 9, 16),
                "USA",
                "test-user"
        ));

        CustomerAddress residential = CustomerAddress.builder()
                .customer(customer)
                .addressType(AddressType.RESIDENTIAL)
                .addressLine1("100 Main St")
                .city("New York")
                .state("NY")
                .country("USA")
                .postalCode("10001")
                .status(AddressStatus.ACTIVE)
                .build();
        addressRepository.save(residential);

        List<CustomerAddress> addresses = addressRepository.findByCustomerId(customer.getId());
        assertEquals(1, addresses.size());

        assertTrue(addressRepository.existsByCustomerIdAndAddressType(customer.getId(), AddressType.RESIDENTIAL));
        assertFalse(addressRepository.existsByCustomerIdAndAddressType(customer.getId(), AddressType.MAILING));

        Optional<CustomerAddress> found = addressRepository.findByCustomerIdAndAddressType(customer.getId(), AddressType.RESIDENTIAL);
        assertTrue(found.isPresent());
        assertEquals("New York", found.get().getCity());
    }
}
