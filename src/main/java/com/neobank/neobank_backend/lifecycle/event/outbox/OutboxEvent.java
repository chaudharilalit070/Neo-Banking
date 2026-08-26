package com.neobank.neobank_backend.lifecycle.event.outbox;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "event_outbox",
        indexes = {
                @Index(
                        name = "idx_event_outbox_status_created",
                        columnList = "status, created_at"
                ),
                @Index(
                        name = "idx_event_outbox_aggregate",
                        columnList = "aggregate_type, aggregate_id"
                )
        }
)
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "aggregate_type",
            nullable = false,
            length = 100
    )
    private String aggregateType;

    @Column(
            name = "aggregate_id",
            nullable = false,
            length = 100
    )
    private String aggregateId;

    @Column(
            name = "event_type",
            nullable = false,
            length = 200
    )
    private String eventType;

    @Lob
    @Column(
            name = "payload",
            nullable = false,
            columnDefinition = "JSON"
    )
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private OutboxEventStatus status;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(
            name = "retry_count",
            nullable = false
    )
    private Integer retryCount = 0;

    protected OutboxEvent() {
    }

    public OutboxEvent(
            String aggregateType,
            String aggregateId,
            String eventType,
            String payload
    ) {

        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.status = OutboxEventStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.retryCount = 0;
    }

    public void markPublished() {

        this.status = OutboxEventStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    public void markFailed() {

        this.status = OutboxEventStatus.FAILED;
        this.retryCount++;
    }

    public Long getId() {
        return id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public OutboxEventStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public Integer getRetryCount() {
        return retryCount;
    }
}