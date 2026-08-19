package com.neobank.neobank_backend.customer.application;


import com.neobank.neobank_backend.customer.api.request.CreateCustomerRequest;
import com.neobank.neobank_backend.customer.api.request.UpdateCustomerRequest;
import com.neobank.neobank_backend.customer.api.respons.CustomerResponse;

import java.util.UUID;

public interface CustomerService {

    CustomerResponse createCustomer(CreateCustomerRequest request);

    CustomerResponse getCustomerById(UUID customerId);

    CustomerResponse updateCustomer(
            UUID customerId,
            UpdateCustomerRequest request
    );
}