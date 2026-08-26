package com.neobank.neobank_backend.lifecycle.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerLifecycleChangedEvent(

        String eventId,

        String eventType,

        int eventVersion,

        LocalDateTime occurredAt,

        String source,

        String correlationId,

        UUID customerId,

        CustomerLifecycleEventData data

) {
}
