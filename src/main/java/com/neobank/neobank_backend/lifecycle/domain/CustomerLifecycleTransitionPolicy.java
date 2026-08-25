package com.neobank.neobank_backend.lifecycle.domain;

public final class CustomerLifecycleTransitionPolicy {

    private CustomerLifecycleTransitionPolicy() {
        // Utility class
    }

    public static CustomerLifecycleTransition resolve(
            CustomerLifecycleStatus currentStatus,
            CustomerLifecycleAction action
    ) {

        if (currentStatus == null) {
            throw new BusinessException(
                    "Current customer lifecycle status is required"
            );
        }

        if (action == null) {
            throw new BusinessException(
                    "Customer lifecycle action is required"
            );
        }

        return switch (action) {

            case START_ONBOARDING -> {

                validate(
                        currentStatus,
                        CustomerLifecycleStatus.PROSPECT,
                        action
                );

                yield new CustomerLifecycleTransition(
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

                yield new CustomerLifecycleTransition(
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

                yield new CustomerLifecycleTransition(
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

                yield new CustomerLifecycleTransition(
                        CustomerLifecycleStatus.ACTIVE,
                        CustomerLifecycleReason.CUSTOMER_REACTIVATED
                );
            }

            case CLOSE -> {

                validateCloseTransition(
                        currentStatus,
                        action
                );

                yield new CustomerLifecycleTransition(
                        CustomerLifecycleStatus.CLOSED,
                        CustomerLifecycleReason.CUSTOMER_CLOSED
                );
            }
        };
    }

    private static void validate(
            CustomerLifecycleStatus currentStatus,
            CustomerLifecycleStatus requiredStatus,
            CustomerLifecycleAction action
    ) {

        if (currentStatus != requiredStatus) {

            throw new BusinessException(
                    "Action "
                            + action
                            + " is not allowed from lifecycle status "
                            + currentStatus
            );
        }
    }

    private static void validateCloseTransition(
            CustomerLifecycleStatus currentStatus,
            CustomerLifecycleAction action
    ) {

        if (currentStatus != CustomerLifecycleStatus.ACTIVE
                && currentStatus != CustomerLifecycleStatus.INACTIVE) {

            throw new BusinessException(
                    "Action "
                            + action
                            + " is not allowed from lifecycle status "
                            + currentStatus
            );
        }
    }
}