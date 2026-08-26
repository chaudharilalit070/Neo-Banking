package com.neobank.neobank_backend.contact.infrastructure;

import com.neobank.neobank_backend.contact.domain.contact.ContactType;
import com.neobank.neobank_backend.contact.domain.contact.CustomerContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaCustomerContactRepository
        extends JpaRepository<CustomerContact, Long> {

    List<CustomerContact> findByCustomerId(UUID customerId);

    Optional<CustomerContact> findByCustomerIdAndContactTypeAndPrimaryTrue(
            UUID customerId,
            ContactType contactType
    );

    boolean existsByCustomerIdAndContactTypeAndContactValue(
            UUID customerId,
            ContactType contactType,
            String contactValue
    );
}
