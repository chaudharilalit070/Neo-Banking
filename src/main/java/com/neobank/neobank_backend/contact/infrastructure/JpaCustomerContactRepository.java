package com.neobank.neobank_backend.contact.infrastructure;
import com.neobank.neobank_backend.contact.domain.contact.ContactType;
import com.neobank.neobank_backend.contact.domain.contact.CustomerContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaCustomerContactRepository
        extends JpaRepository<CustomerContact, Long> {

    List<CustomerContact> findByCustomerId(Long customerId);

    Optional<CustomerContact> findByCustomerIdAndContactTypeAndPrimaryTrue(
            Long customerId,
            ContactType contactType
    );

    boolean existsByCustomerIdAndContactTypeAndContactValue(
            Long customerId,
            ContactType contactType,
            String contactValue
    );
}