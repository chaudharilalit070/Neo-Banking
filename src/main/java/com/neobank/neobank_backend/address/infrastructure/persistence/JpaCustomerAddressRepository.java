package com.neobank.neobank_backend.address.infrastructure.persistence;

import com.neobank.neobank_backend.address.domain.AddressType;
import com.neobank.neobank_backend.address.domain.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaCustomerAddressRepository
        extends JpaRepository<CustomerAddress, Long> {

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
