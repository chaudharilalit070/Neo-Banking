package com.neobank.neobank_backend.common.exception;


public final class CustomerLifecycleTransitionPolicy {

    private CustomerLifecycleTransitionPolicy() {
        // Utility class
    }

    public static CustomerLifecycleTransition resolve(
            CustomerLifecycleStatus currentStatus,
            CustomerLifecycleAction action
    ) {

        if (currentStatus == null) {
            throw new ConflictException(
                    "Current customer lifecycle status is required"
            );
        }

        if (action == null) {
            throw new ConflictException(
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

            throw new ConflictException(
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

            throw new ConflictException(
                    "Action "
                            + action
                            + " is not allowed from lifecycle status "
                            + currentStatus
            );
        }
    }
}