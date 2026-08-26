package com.neobank.neobank_backend.audit.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuditEvent {

    private Long id;

    private UUID customerId;

    private AuditAction action;

    private String previousStatus;

    private String newStatus;

    private String reason;

    private String actorId;

    private String actorType;

    private String correlationId;

    private LocalDateTime occurredAt;

    private String source;

    public AuditEvent(
            UUID customerId,
            AuditAction action,
            String previousStatus,
            String newStatus,
            String reason,
            String actorId,
            String actorType,
            String correlationId,
            LocalDateTime occurredAt,
            String source
    ) {
        this.customerId = customerId;
        this.action = action;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.reason = reason;
        this.actorId = actorId;
        this.actorType = actorType;
        this.correlationId = correlationId;
        this.occurredAt = occurredAt;
        this.source = source;
    }

    public AuditEvent(
            Long id,
            UUID customerId,
            AuditAction action,
            String previousStatus,
            String newStatus,
            String reason,
            String actorId,
            String actorType,
            String correlationId,
            LocalDateTime occurredAt,
            String source
    ) {
        this(
                customerId,
                action,
                previousStatus,
                newStatus,
                reason,
                actorId,
                actorType,
                correlationId,
                occurredAt,
                source
        );
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public AuditAction getAction() {
        return action;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public String getReason() {
        return reason;
    }

    public String getActorId() {
        return actorId;
    }

    public String getActorType() {
        return actorType;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public String getSource() {
        return source;
    }
}
