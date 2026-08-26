package com.neobank.neobank_backend.customer.application;

import com.neobank.neobank_backend.common.constants.ErrorCodes;
import com.neobank.neobank_backend.common.exception.ResourceNotFoundException;
import com.neobank.neobank_backend.customer.api.request.CreateCustomerRequest;
import com.neobank.neobank_backend.customer.api.request.UpdateCustomerRequest;
import com.neobank.neobank_backend.customer.api.respons.CustomerResponse;
import com.neobank.neobank_backend.customer.domain.Customer;
import com.neobank.neobank_backend.customer.domain.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private static final String SYSTEM_USER = "SYSTEM";

    private final CustomerRepository customerRepository;
    private final CustomerNumberGenerator customerNumberGenerator;

    @Override
    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {

        String customerNumber = customerNumberGenerator.generate();

        while (customerRepository.existsByCustomerNumber(customerNumber)) {
            customerNumber = customerNumberGenerator.generate();
        }

        Customer customer = Customer.create(
                customerNumber,
                request.customerType(),
                request.firstName(),
                request.middleName(),
                request.lastName(),
                request.dateOfBirth(),
                request.nationality(),
                SYSTEM_USER
        );

        Customer savedCustomer = customerRepository.save(customer);

        return mapToResponse(savedCustomer);
    }

    @Override
    public CustomerResponse getCustomerById(UUID customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCodes.CUSTOMER_NOT_FOUND,
                        "Customer not found: " + customerId
                ));

        return mapToResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(
            UUID customerId,
            UpdateCustomerRequest request
    ) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCodes.CUSTOMER_NOT_FOUND,
                        "Customer not found: " + customerId
                ));

        customer.updateProfile(
                request.firstName(),
                request.middleName(),
                request.lastName(),
                request.dateOfBirth(),
                request.nationality(),
                SYSTEM_USER
        );

        Customer updatedCustomer = customerRepository.save(customer);

        return mapToResponse(updatedCustomer);
    }

    private CustomerResponse mapToResponse(Customer customer) {

        return new CustomerResponse(
                customer.getId(),
                customer.getCustomerNumber(),
                customer.getCustomerType(),
                customer.getCustomerStatus(),
                customer.getFirstName(),
                customer.getMiddleName(),
                customer.getLastName(),
                customer.getDateOfBirth(),
                customer.getNationality(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}
