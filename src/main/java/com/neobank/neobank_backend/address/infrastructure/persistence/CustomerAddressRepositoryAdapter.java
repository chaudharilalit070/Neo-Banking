package com.neobank.neobank_backend.address.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerAddressRepositoryAdapter
        implements CustomerAddressRepository {

    private final JpaCustomerAddressRepository
            jpaCustomerAddressRepository;


    @Override
    public CustomerAddress save(
            CustomerAddress customerAddress
    ) {
        return jpaCustomerAddressRepository.save(customerAddress);
    }


    @Override
    public Optional<CustomerAddress> findById(
            Long id
    ) {
        return jpaCustomerAddressRepository.findById(id);
    }


    @Override
    public List<CustomerAddress> findByCustomerId(
            Long customerId
    ) {
        return jpaCustomerAddressRepository
                .findByCustomerId(customerId);
    }


    @Override
    public Optional<CustomerAddress>
    findByCustomerIdAndAddressType(
            Long customerId,
            AddressType addressType
    ) {
        return jpaCustomerAddressRepository
                .findByCustomerIdAndAddressType(
                        customerId,
                        addressType
                );
    }


    @Override
    public boolean existsByCustomerIdAndAddressType(
            Long customerId,
            AddressType addressType
    ) {
        return jpaCustomerAddressRepository
                .existsByCustomerIdAndAddressType(
                        customerId,
                        addressType
                );
    }
}