package com.neobank.neobank_backend.contact.domain.contact;

import java.util.List;
import java.util.Optional;

public interface CustomerContactRepository {

    CustomerContact save(CustomerContact customerContact);

    Optional<CustomerContact> findById(Long id);

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