package com.neobank.neobank_backend.audit.api;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditEventResponse(

        UUID customerId,

        String action,

        String previousStatus,

        String newStatus,

        String reason,

        String actorId,

        String actorType,

        String correlationId,

        LocalDateTime occurredAt,

        String source

) {
}
