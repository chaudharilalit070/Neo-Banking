package com.neobank.neobank_backend.address.application;

import com.neobank.neobank_backend.address.api.request.AddCustomerAddressRequest;
import com.neobank.neobank_backend.address.api.response.CustomerAddressResponse;
import com.neobank.neobank_backend.address.domain.AddressStatus;
import com.neobank.neobank_backend.address.domain.CustomerAddress;
import com.neobank.neobank_backend.address.domain.CustomerAddressRepository;
import com.neobank.neobank_backend.common.constants.ErrorCodes;
import com.neobank.neobank_backend.common.exception.ConflictException;
import com.neobank.neobank_backend.common.exception.ResourceNotFoundException;
import com.neobank.neobank_backend.customer.domain.Customer;
import com.neobank.neobank_backend.customer.domain.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerAddressService {

    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository customerAddressRepository;

    public CustomerAddressResponse addAddress(
            UUID customerId,
            AddCustomerAddressRequest request
    ) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ErrorCodes.CUSTOMER_NOT_FOUND,
                                "Customer not found with id: " + customerId
                        )
                );

        boolean addressExists =
                customerAddressRepository.existsByCustomerIdAndAddressType(
                        customerId,
                        request.getAddressType()
                );

        if (addressExists) {
            throw new ConflictException(
                    ErrorCodes.ADDRESS_ALREADY_EXISTS,
                    "Address of type "
                            + request.getAddressType()
                            + " already exists for this customer"
            );
        }

        CustomerAddress customerAddress =
                CustomerAddress.builder()
                        .customer(customer)
                        .addressType(request.getAddressType())
                        .addressLine1(normalize(request.getAddressLine1()))
                        .addressLine2(normalize(request.getAddressLine2()))
                        .landmark(normalize(request.getLandmark()))
                        .city(normalize(request.getCity()))
                        .district(normalize(request.getDistrict()))
                        .state(normalize(request.getState()))
                        .country(normalize(request.getCountry()))
                        .postalCode(normalize(request.getPostalCode()))
                        .status(AddressStatus.ACTIVE)
                        .build();

        CustomerAddress savedAddress =
                customerAddressRepository.save(customerAddress);

        return mapToResponse(savedAddress);
    }

    @Transactional(readOnly = true)
    public List<CustomerAddressResponse> getCustomerAddresses(UUID customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    ErrorCodes.CUSTOMER_NOT_FOUND,
                    "Customer not found with id: " + customerId
            );
        }

        return customerAddressRepository
                .findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerAddressResponse getAddressById(
            UUID customerId,
            Long addressId
    ) {
        return mapToResponse(
                getCustomerAddressForCustomer(customerId, addressId)
        );
    }

    public CustomerAddressResponse updateAddress(
            UUID customerId,
            Long addressId,
            AddCustomerAddressRequest request
    ) {
        CustomerAddress customerAddress =
                getCustomerAddressForCustomer(customerId, addressId);

        if (!customerAddress.getAddressType().equals(request.getAddressType())) {
            customerAddressRepository
                    .findByCustomerIdAndAddressType(
                            customerId,
                            request.getAddressType()
                    )
                    .ifPresent(existingAddress -> {
                        if (!existingAddress.getId().equals(addressId)) {
                            throw new ConflictException(
                                    ErrorCodes.ADDRESS_ALREADY_EXISTS,
                                    "Address of type "
                                            + request.getAddressType()
                                            + " already exists for this customer"
                            );
                        }
                    });
        }

        customerAddress.setAddressType(request.getAddressType());
        customerAddress.setAddressLine1(normalize(request.getAddressLine1()));
        customerAddress.setAddressLine2(normalize(request.getAddressLine2()));
        customerAddress.setLandmark(normalize(request.getLandmark()));
        customerAddress.setCity(normalize(request.getCity()));
        customerAddress.setDistrict(normalize(request.getDistrict()));
        customerAddress.setState(normalize(request.getState()));
        customerAddress.setCountry(normalize(request.getCountry()));
        customerAddress.setPostalCode(normalize(request.getPostalCode()));

        CustomerAddress updatedAddress =
                customerAddressRepository.save(customerAddress);

        return mapToResponse(updatedAddress);
    }

    public CustomerAddressResponse deactivateAddress(
            UUID customerId,
            Long addressId
    ) {
        CustomerAddress customerAddress =
                getCustomerAddressForCustomer(customerId, addressId);

        customerAddress.setStatus(AddressStatus.INACTIVE);

        CustomerAddress updatedAddress =
                customerAddressRepository.save(customerAddress);

        return mapToResponse(updatedAddress);
    }

    private CustomerAddress getCustomerAddressForCustomer(
            UUID customerId,
            Long addressId
    ) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    ErrorCodes.CUSTOMER_NOT_FOUND,
                    "Customer not found with id: " + customerId
            );
        }

        CustomerAddress customerAddress =
                customerAddressRepository.findById(addressId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        ErrorCodes.ADDRESS_NOT_FOUND,
                                        "Customer address not found with id: "
                                                + addressId
                                )
                        );

        if (!customerAddress.getCustomer().getId().equals(customerId)) {
            throw new ConflictException(
                    ErrorCodes.ADDRESS_DOES_NOT_BELONG_TO_CUSTOMER,
                    "Address does not belong to this customer"
            );
        }

        return customerAddress;
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private CustomerAddressResponse mapToResponse(CustomerAddress address) {
        return CustomerAddressResponse.builder()
                .id(address.getId())
                .customerId(address.getCustomer().getId())
                .addressType(address.getAddressType())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .landmark(address.getLandmark())
                .city(address.getCity())
                .district(address.getDistrict())
                .state(address.getState())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .status(address.getStatus())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}
