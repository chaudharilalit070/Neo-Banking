package com.neobank.neobank_backend.lifecycle.api.response;


import java.time.LocalDateTime;

public record CustomerLifecycleResponse(

        Long id,

        Long customerId,

        CustomerLifecycleStatus previousStatus,

        CustomerLifecycleStatus currentStatus,

        CustomerLifecycleReason reason,

        LocalDateTime effectiveAt,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {

    public static CustomerLifecycleResponse from(
            CustomerLifecycle lifecycle
    ) {

        return new CustomerLifecycleResponse(
                lifecycle.getId(),
                lifecycle.getCustomer().getId(),
                lifecycle.getPreviousStatus(),
                lifecycle.getCurrentStatus(),
                lifecycle.getReason(),
                lifecycle.getEffectiveAt(),
                lifecycle.getCreatedAt(),
                lifecycle.getUpdatedAt()
        );
    }
}