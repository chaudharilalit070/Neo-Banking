package com.neobank.neobank_backend.address.infrastructure.persistence;

import com.neobank.neobank_backend.address.domain.AddressType;
import com.neobank.neobank_backend.address.domain.CustomerAddress;
import com.neobank.neobank_backend.address.domain.CustomerAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CustomerAddressRepositoryAdapter
        implements CustomerAddressRepository {

    private final JpaCustomerAddressRepository jpaCustomerAddressRepository;

    @Override
    public CustomerAddress save(CustomerAddress customerAddress) {
        return jpaCustomerAddressRepository.save(customerAddress);
    }

    @Override
    public Optional<CustomerAddress> findById(Long id) {
        return jpaCustomerAddressRepository.findById(id);
    }

    @Override
    public List<CustomerAddress> findByCustomerId(UUID customerId) {
        return jpaCustomerAddressRepository.findByCustomerId(customerId);
    }

    @Override
    public Optional<CustomerAddress> findByCustomerIdAndAddressType(
            UUID customerId,
            AddressType addressType
    ) {
        return jpaCustomerAddressRepository.findByCustomerIdAndAddressType(
                customerId,
                addressType
        );
    }

    @Override
    public boolean existsByCustomerIdAndAddressType(
            UUID customerId,
            AddressType addressType
    ) {
        return jpaCustomerAddressRepository.existsByCustomerIdAndAddressType(
                customerId,
                addressType
        );
    }
}
