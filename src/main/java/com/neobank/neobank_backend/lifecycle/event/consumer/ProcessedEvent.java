package com.neobank.neobank_backend.lifecycle.event.consumer;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "processed_event",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_processed_event_event_id",
                        columnNames = "event_id"
                )
        }
)
public class ProcessedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "event_id",
            nullable = false,
            length = 100
    )
    private String eventId;

    @Column(
            name = "event_type",
            nullable = false,
            length = 200
    )
    private String eventType;

    @Column(
            name = "processed_at",
            nullable = false
    )
    private LocalDateTime processedAt;

    protected ProcessedEvent() {
    }

    public ProcessedEvent(
            String eventId,
            String eventType
    ) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.processedAt = LocalDateTime.now();
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
}
