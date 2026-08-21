package com.neobank.neobank_backend.address.domain;

import java.util.List;
import java.util.Optional;

public interface CustomerAddressRepository {

    CustomerAddress save(CustomerAddress customerAddress);

    Optional<CustomerAddress> findById(Long id);

    List<CustomerAddress> findByCustomerId(Long customerId);

    Optional<CustomerAddress> findByCustomerIdAndAddressType(
            Long customerId,
            AddressType addressType
    );

    boolean existsByCustomerIdAndAddressType(
            Long customerId,
            AddressType addressType
    );
}