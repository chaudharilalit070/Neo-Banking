package com.neobank.neobank_backend.lifecycle.domain;

import com.neobank.neobank_backend.common.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class CustomerLifecycleTransitionPolicyTest {

    @Test
    @DisplayName("START_ONBOARDING: PROSPECT -> ONBOARDING")
    void testStartOnboardingFromProspect() {
        CustomerLifecycleTransition transition = CustomerLifecycleTransitionPolicy.resolve(
                CustomerLifecycleStatus.PROSPECT,
                CustomerLifecycleAction.START_ONBOARDING
        );

        assertNotNull(transition);
        assertEquals(CustomerLifecycleStatus.ONBOARDING, transition.newStatus());
        assertEquals(CustomerLifecycleReason.ONBOARDING_STARTED, transition.reason());
    }

    @Test
    @DisplayName("COMPLETE_ONBOARDING: ONBOARDING -> ACTIVE")
    void testCompleteOnboardingFromOnboarding() {
        CustomerLifecycleTransition transition = CustomerLifecycleTransitionPolicy.resolve(
                CustomerLifecycleStatus.ONBOARDING,
                CustomerLifecycleAction.COMPLETE_ONBOARDING
        );

        assertNotNull(transition);
        assertEquals(CustomerLifecycleStatus.ACTIVE, transition.newStatus());
        assertEquals(CustomerLifecycleReason.ONBOARDING_COMPLETED, transition.reason());
    }

    @Test
    @DisplayName("DEACTIVATE: ACTIVE -> INACTIVE")
    void testDeactivateFromActive() {
        CustomerLifecycleTransition transition = CustomerLifecycleTransitionPolicy.resolve(
                CustomerLifecycleStatus.ACTIVE,
                CustomerLifecycleAction.DEACTIVATE
        );

        assertNotNull(transition);
        assertEquals(CustomerLifecycleStatus.INACTIVE, transition.newStatus());
        assertEquals(CustomerLifecycleReason.CUSTOMER_DEACTIVATED, transition.reason());
    }

    @Test
    @DisplayName("REACTIVATE: INACTIVE -> ACTIVE")
    void testReactivateFromInactive() {
        CustomerLifecycleTransition transition = CustomerLifecycleTransitionPolicy.resolve(
                CustomerLifecycleStatus.INACTIVE,
                CustomerLifecycleAction.REACTIVATE
        );

        assertNotNull(transition);
        assertEquals(CustomerLifecycleStatus.ACTIVE, transition.newStatus());
        assertEquals(CustomerLifecycleReason.CUSTOMER_REACTIVATED, transition.reason());
    }

    @ParameterizedTest
    @CsvSource({
            "ACTIVE, CUSTOMER_CLOSED",
            "INACTIVE, CUSTOMER_CLOSED"
    })
    @DisplayName("CLOSE: Allowed from ACTIVE and INACTIVE")
    void testCloseAllowed(CustomerLifecycleStatus currentStatus, CustomerLifecycleReason expectedReason) {
        CustomerLifecycleTransition transition = CustomerLifecycleTransitionPolicy.resolve(
                currentStatus,
                CustomerLifecycleAction.CLOSE
        );

        assertNotNull(transition);
        assertEquals(CustomerLifecycleStatus.CLOSED, transition.newStatus());
        assertEquals(expectedReason, transition.reason());
    }

    @Test
    @DisplayName("Invalid transitions should throw BusinessException")
    void testInvalidTransitions() {
        // START_ONBOARDING not allowed from ACTIVE
        assertThrows(BusinessException.class, () ->
                CustomerLifecycleTransitionPolicy.resolve(
                        CustomerLifecycleStatus.ACTIVE,
                        CustomerLifecycleAction.START_ONBOARDING
                )
        );

        // COMPLETE_ONBOARDING not allowed from PROSPECT
        assertThrows(BusinessException.class, () ->
                CustomerLifecycleTransitionPolicy.resolve(
                        CustomerLifecycleStatus.PROSPECT,
                        CustomerLifecycleAction.COMPLETE_ONBOARDING
                )
        );

        // DEACTIVATE not allowed from PROSPECT or ONBOARDING
        assertThrows(BusinessException.class, () ->
                CustomerLifecycleTransitionPolicy.resolve(
                        CustomerLifecycleStatus.ONBOARDING,
                        CustomerLifecycleAction.DEACTIVATE
                )
        );

        // REACTIVATE not allowed from ACTIVE
        assertThrows(BusinessException.class, () ->
                CustomerLifecycleTransitionPolicy.resolve(
                        CustomerLifecycleStatus.ACTIVE,
                        CustomerLifecycleAction.REACTIVATE
                )
        );

        // CLOSE not allowed from PROSPECT
        assertThrows(BusinessException.class, () ->
                CustomerLifecycleTransitionPolicy.resolve(
                        CustomerLifecycleStatus.PROSPECT,
                        CustomerLifecycleAction.CLOSE
                )
        );

        // Any action from CLOSED not allowed
        assertThrows(BusinessException.class, () ->
                CustomerLifecycleTransitionPolicy.resolve(
                        CustomerLifecycleStatus.CLOSED,
                        CustomerLifecycleAction.REACTIVATE
                )
        );
    }

    @Test
    @DisplayName("Null arguments should throw BusinessException")
    void testNullArguments() {
        assertThrows(BusinessException.class, () ->
                CustomerLifecycleTransitionPolicy.resolve(null, CustomerLifecycleAction.START_ONBOARDING));
        assertThrows(BusinessException.class, () ->
                CustomerLifecycleTransitionPolicy.resolve(CustomerLifecycleStatus.PROSPECT, null));
    }
}
