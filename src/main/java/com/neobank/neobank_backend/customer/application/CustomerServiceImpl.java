package com.neobank.neobank_backend.customer.application;

import com.neobank.neobank_backend.audit.application.AuditService;
import com.neobank.neobank_backend.audit.domain.ActorType;
import com.neobank.neobank_backend.common.constants.ErrorCodes;
import com.neobank.neobank_backend.common.exception.ResourceNotFoundException;
import com.neobank.neobank_backend.customer.api.request.CreateCustomerRequest;
import com.neobank.neobank_backend.customer.api.request.UpdateCustomerRequest;
import com.neobank.neobank_backend.customer.api.respons.CustomerResponse;
import com.neobank.neobank_backend.customer.domain.Customer;
import com.neobank.neobank_backend.customer.domain.CustomerRepository;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycle;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycleReason;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycleRepository;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycleStatus;
import com.neobank.neobank_backend.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerNumberGenerator customerNumberGenerator;
    private final CustomerLifecycleRepository lifecycleRepository;
    private final AuditService auditService;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        String customerNumber = customerNumberGenerator.generate();

        while (customerRepository.existsByCustomerNumber(customerNumber)) {
            customerNumber = customerNumberGenerator.generate();
        }

        String actorId = currentUserProvider.getCurrentUserId();
        String correlationId = resolveCorrelationId();

        Customer customer = Customer.create(
                customerNumber,
                request.customerType(),
                request.firstName(),
                request.middleName(),
                request.lastName(),
                request.dateOfBirth(),
                request.nationality(),
                actorId
        );

        Customer savedCustomer = customerRepository.save(customer);

        CustomerLifecycle initialLifecycle = CustomerLifecycle.builder()
                .customer(savedCustomer)
                .previousStatus(null)
                .currentStatus(CustomerLifecycleStatus.PROSPECT)
                .reason(CustomerLifecycleReason.INITIAL)
                .effectiveAt(LocalDateTime.now())
                .build();
        lifecycleRepository.save(initialLifecycle);

        auditService.recordCustomerCreation(
                savedCustomer.getId(),
                actorId,
                ActorType.EMPLOYEE.name(),
                correlationId
        );

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

        String actorId = currentUserProvider.getCurrentUserId();
        String correlationId = resolveCorrelationId();

        customer.updateProfile(
                request.firstName(),
                request.middleName(),
                request.lastName(),
                request.dateOfBirth(),
                request.nationality(),
                actorId
        );

        Customer updatedCustomer = customerRepository.save(customer);

        auditService.recordCustomerUpdate(
                customerId,
                actorId,
                ActorType.EMPLOYEE.name(),
                correlationId
        );

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

    private String resolveCorrelationId() {
        String correlationId = MDC.get("correlationId");
        if (correlationId == null || correlationId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return correlationId;
    }
}
