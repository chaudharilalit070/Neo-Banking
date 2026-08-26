package com.neobank.neobank_backend.lifecycle.application;

import com.neobank.neobank_backend.audit.application.AuditService;
import com.neobank.neobank_backend.audit.domain.ActorType;
import com.neobank.neobank_backend.common.constants.ErrorCodes;
import com.neobank.neobank_backend.common.exception.ResourceNotFoundException;
import com.neobank.neobank_backend.customer.domain.Customer;
import com.neobank.neobank_backend.customer.domain.CustomerRepository;
import com.neobank.neobank_backend.customer.domain.CustomerStatus;
import com.neobank.neobank_backend.lifecycle.api.request.CustomerLifecycleActionRequest;
import com.neobank.neobank_backend.lifecycle.api.response.CustomerLifecycleResponse;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycle;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycleRepository;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycleStatus;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycleTransition;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycleTransitionPolicy;
import com.neobank.neobank_backend.lifecycle.event.CustomerLifecycleChangedEvent;
import com.neobank.neobank_backend.lifecycle.event.CustomerLifecycleEventFactory;
import com.neobank.neobank_backend.lifecycle.event.outbox.OutboxEventService;
import com.neobank.neobank_backend.security.CurrentUserProvider;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CustomerLifecycleService {

    private final CustomerRepository customerRepository;
    private final CustomerLifecycleRepository lifecycleRepository;
    private final AuditService auditService;
    private final OutboxEventService outboxEventService;
    private final CustomerLifecycleEventFactory eventFactory;
    private final CurrentUserProvider currentUserProvider;

    public CustomerLifecycleService(
            CustomerRepository customerRepository,
            CustomerLifecycleRepository lifecycleRepository,
            AuditService auditService,
            OutboxEventService outboxEventService,
            CustomerLifecycleEventFactory eventFactory,
            CurrentUserProvider currentUserProvider
    ) {
        this.customerRepository = customerRepository;
        this.lifecycleRepository = lifecycleRepository;
        this.auditService = auditService;
        this.outboxEventService = outboxEventService;
        this.eventFactory = eventFactory;
        this.currentUserProvider = currentUserProvider;
    }

    public CustomerLifecycleResponse applyAction(
            UUID customerId,
            CustomerLifecycleActionRequest request
    ) {
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCodes.CUSTOMER_NOT_FOUND,
                        "Customer not found with id: " + customerId
                ));

        CustomerLifecycleStatus currentStatus = getCurrentStatus(customerId);

        CustomerLifecycleTransition transition =
                CustomerLifecycleTransitionPolicy.resolve(
                        currentStatus,
                        request.action()
                );

        CustomerLifecycle lifecycle = CustomerLifecycle.builder()
                .customer(customer)
                .previousStatus(currentStatus)
                .currentStatus(transition.newStatus())
                .reason(transition.reason())
                .effectiveAt(LocalDateTime.now())
                .build();

        CustomerLifecycle saved = lifecycleRepository.save(lifecycle);

        syncCustomerStatus(customer, transition.newStatus());

        String actorId = currentUserProvider.getCurrentUserId();
        String correlationId = resolveCorrelationId();

        auditService.recordLifecycleChange(
                customerId,
                currentStatus != null ? currentStatus.name() : null,
                transition.newStatus().name(),
                transition.reason().name(),
                actorId,
                ActorType.EMPLOYEE.name(),
                correlationId
        );

        CustomerLifecycleChangedEvent event =
                eventFactory.create(saved, correlationId);
        outboxEventService.createLifecycleEvent(event);

        return CustomerLifecycleResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public CustomerLifecycleResponse getCurrentLifecycle(UUID customerId) {
        verifyCustomerExists(customerId);

        return lifecycleRepository
                .findLatestByCustomerId(customerId)
                .map(CustomerLifecycleResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCodes.LIFECYCLE_NOT_FOUND,
                        "Lifecycle not found for customer: " + customerId
                ));
    }

    @Transactional(readOnly = true)
    public List<CustomerLifecycleResponse> getLifecycleHistory(UUID customerId) {
        verifyCustomerExists(customerId);

        return lifecycleRepository
                .findAllByCustomerId(customerId)
                .stream()
                .map(CustomerLifecycleResponse::from)
                .toList();
    }

    private CustomerLifecycleStatus getCurrentStatus(UUID customerId) {
        return lifecycleRepository
                .findLatestByCustomerId(customerId)
                .map(CustomerLifecycle::getCurrentStatus)
                .orElse(CustomerLifecycleStatus.PROSPECT);
    }

    private void verifyCustomerExists(UUID customerId) {
        if (!customerRepository.findById(customerId).isPresent()) {
            throw new ResourceNotFoundException(
                    ErrorCodes.CUSTOMER_NOT_FOUND,
                    "Customer not found with id: " + customerId
            );
        }
    }

    private void syncCustomerStatus(
            Customer customer,
            CustomerLifecycleStatus lifecycleStatus
    ) {
        CustomerStatus mapped = switch (lifecycleStatus) {
            case PROSPECT -> CustomerStatus.PROSPECT;
            case ONBOARDING -> CustomerStatus.ONBOARDING;
            case ACTIVE -> CustomerStatus.ACTIVE;
            case INACTIVE -> CustomerStatus.INACTIVE;
            case CLOSED -> CustomerStatus.CLOSED;
        };

        customer.updateStatus(mapped, currentUserProvider.getCurrentUserId());
        customerRepository.save(customer);
    }

    private String resolveCorrelationId() {
        String correlationId = MDC.get("correlationId");
        if (correlationId == null || correlationId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return correlationId;
    }
}
