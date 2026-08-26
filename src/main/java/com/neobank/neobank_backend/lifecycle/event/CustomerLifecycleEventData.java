package com.neobank.neobank_backend.lifecycle.event;

import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycleReason;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycleStatus;

import java.time.LocalDateTime;

public record CustomerLifecycleEventData(

        Long lifecycleId,

        CustomerLifecycleStatus previousStatus,

        CustomerLifecycleStatus currentStatus,

        CustomerLifecycleReason reason,

        LocalDateTime effectiveAt

) {
}
