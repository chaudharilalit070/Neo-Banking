package com.neobank.neobank_backend.contact.domain.contact;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerContactRepository {

    CustomerContact save(CustomerContact customerContact);

    Optional<CustomerContact> findById(Long id);

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
