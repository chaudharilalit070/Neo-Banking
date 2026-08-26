package com.neobank.neobank_backend.address.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerAddressRepository {

    CustomerAddress save(CustomerAddress customerAddress);

    Optional<CustomerAddress> findById(Long id);

    List<CustomerAddress> findByCustomerId(UUID customerId);

    Optional<CustomerAddress> findByCustomerIdAndAddressType(
            UUID customerId,
            AddressType addressType
    );

    boolean existsByCustomerIdAndAddressType(
            UUID customerId,
            AddressType addressType
    );
}
