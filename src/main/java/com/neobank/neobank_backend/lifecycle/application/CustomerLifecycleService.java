package com.neobank.neobank_backend.lifecycle.application;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class CustomerLifecycleService {

    private final CustomerRepository customerRepository;
    private final CustomerLifecycleRepository lifecycleRepository;

    public CustomerLifecycleService(
            CustomerRepository customerRepository,
            CustomerLifecycleRepository lifecycleRepository
    ) {
        this.customerRepository = customerRepository;
        this.lifecycleRepository = lifecycleRepository;
    }


    /**
     * Apply a lifecycle action to a customer.
     */
    public CustomerLifecycleResponse applyAction(
            Long customerId,
            CustomerLifecycleActionRequest request
    ) {

        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with id: " + customerId
                        )
                );

        CustomerLifecycleStatus currentStatus =
                getCurrentStatus(customerId);

        Transition transition =
                resolveTransition(
                        currentStatus,
                        request.action()
                );

        CustomerLifecycle lifecycle =
                CustomerLifecycle.builder()
                        .customer(customer)
                        .previousStatus(currentStatus)
                        .currentStatus(transition.newStatus())
                        .reason(transition.reason())
                        .effectiveAt(LocalDateTime.now())
                        .build();

        CustomerLifecycle saved =
                lifecycleRepository.save(lifecycle);

        return CustomerLifecycleResponse.from(saved);
    }


    /**
     * Get the customer's current lifecycle status.
     */
    @Transactional(readOnly = true)
    public CustomerLifecycleResponse getCurrentLifecycle(
            Long customerId
    ) {

        verifyCustomerExists(customerId);

        return lifecycleRepository
                .findLatestByCustomerId(customerId)
                .map(CustomerLifecycleResponse::from)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Lifecycle not found for customer: "
                                        + customerId
                        )
                );
    }


    /**
     * Get the complete lifecycle history.
     */
    @Transactional(readOnly = true)
    public java.util.List<CustomerLifecycleResponse> getLifecycleHistory(
            Long customerId
    ) {

        verifyCustomerExists(customerId);

        return lifecycleRepository
                .findAllByCustomerId(customerId)
                .stream()
                .map(CustomerLifecycleResponse::from)
                .toList();
    }


    private CustomerLifecycleStatus getCurrentStatus(
            Long customerId
    ) {

        return lifecycleRepository
                .findLatestByCustomerId(customerId)
                .map(CustomerLifecycle::getCurrentStatus)
                .orElse(CustomerLifecycleStatus.PROSPECT);
    }


    private void verifyCustomerExists(
            Long customerId
    ) {

        customerRepository
                .findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with id: " + customerId
                        )
                );
    }


    /**
     * Resolve and validate the requested lifecycle transition.
     */
    private Transition resolveTransition(
            CustomerLifecycleStatus currentStatus,
            CustomerLifecycleAction action
    ) {

        return switch (action) {

            case START_ONBOARDING -> {

                validate(
                        currentStatus,
                        CustomerLifecycleStatus.PROSPECT,
                        action
                );

                yield new Transition(
                        CustomerLifecycleStatus.ONBOARDING,
                        CustomerLifecycleReason.ONBOARDING_STARTED
                );
            }

            case COMPLETE_ONBOARDING -> {

                validate(
                        currentStatus,
                        CustomerLifecycleStatus.ONBOARDING,
                        action
                );

                yield new Transition(
                        CustomerLifecycleStatus.ACTIVE,
                        CustomerLifecycleReason.ONBOARDING_COMPLETED
                );
            }

            case DEACTIVATE -> {

                validate(
                        currentStatus,
                        CustomerLifecycleStatus.ACTIVE,
                        action
                );

                yield new Transition(
                        CustomerLifecycleStatus.INACTIVE,
                        CustomerLifecycleReason.CUSTOMER_DEACTIVATED
                );
            }

            case REACTIVATE -> {

                validate(
                        currentStatus,
                        CustomerLifecycleStatus.INACTIVE,
                        action
                );

                yield new Transition(
                        CustomerLifecycleStatus.ACTIVE,
                        CustomerLifecycleReason.CUSTOMER_REACTIVATED
                );
            }

            case CLOSE -> {

                if (currentStatus != CustomerLifecycleStatus.ACTIVE
                        && currentStatus != CustomerLifecycleStatus.INACTIVE) {

                    throw new BusinessException(
                            "Customer cannot be closed from status: "
                                    + currentStatus
                    );
                }

                yield new Transition(
                        CustomerLifecycleStatus.CLOSED,
                        CustomerLifecycleReason.CUSTOMER_CLOSED
                );
            }
        };
    }


    private void validate(
            CustomerLifecycleStatus actualStatus,
            CustomerLifecycleStatus requiredStatus,
            CustomerLifecycleAction action
    ) {

        if (actualStatus != requiredStatus) {

            throw new BusinessException(
                    "Action "
                            + action
                            + " is not allowed when customer lifecycle "
                            + "status is "
                            + actualStatus
            );
        }
    }


    /**
     * Internal transition result.
     */
    private record Transition(
            CustomerLifecycleStatus newStatus,
            CustomerLifecycleReason reason
    ) {
    }
}