# Kafka Messaging, Outbox & Idempotency Flow

## Event-Driven Architecture Overview

The Customer Microservice guarantees **At-Least-Once Delivery** and **Exactly-Once Processing Semantics** across microservice boundaries using the Transactional Outbox Pattern and an Idempotent Consumer.

```
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                                TRANSACTIONAL BOUNDARY                                       │
│                                                                                             │
│  1. Business Service Mutation ───────► INSERT/UPDATE customer, customer_lifecycle           │
│  2. Audit Log ───────────────────────► INSERT INTO audit_events                             │
│  3. Outbox Table ────────────────────► INSERT INTO event_outbox (status = 'PENDING')        │
└────────────────────────────────────────────────┬────────────────────────────────────────────┘
                                                 │ (Commit Transaction)
                                                 ▼
                             ┌───────────────────────────────────────┐
                             │ Outbox Publisher Poller (@Scheduled)  │
                             └───────────────────┬───────────────────┘
                                                 │
                                                 │ KafkaTemplate.send()
                                                 ▼
                             ┌───────────────────────────────────────┐
                             │       Apache Kafka Cluster            │
                             │  Topic: customer.lifecycle.changed    │
                             └───────────────────┬───────────────────┘
                                                 │
                                                 │ @KafkaListener
                                                 ▼
                             ┌───────────────────────────────────────┐
                             │     CustomerLifecycleEventConsumer    │
                             └───────────────────┬───────────────────┘
                                                 │
                               ┌─────────────────┴─────────────────┐
                               ▼                                   ▼
                    [Check Event Duplicate]                 [Process Event]
                     processedEventRepo?                     Execute Action
                               │                                   │
                     If Exists: SKIP / ACK                         ▼
                                                        INSERT INTO processed_event
                                                          (uk_processed_event_event_id)
```

---

## Outbox Publisher Polling & Retries

1. **Scheduled Polling**: Every 5000ms, `CustomerLifecycleOutboxPublisher` polls `findTop100ByStatusOrderByCreatedAtAsc(PENDING)`.
2. **Kafka Asynchronous Dispatch**:
   - On success: updates outbox status to `PUBLISHED` and sets `published_at = NOW()`.
   - On failure: increments `retry_count`, sets `error_message`, and updates status to `FAILED` for subsequent redelivery attempts.

---

## Idempotent Consumer & Deduplication

1. **Unique Constraint Guard**: Table `processed_event` has a unique index on `event_id` (`uk_processed_event_event_id`).
2. **Double-Check Strategy**:
   - Step 1: Query `processedEventRepository.existsByEventId(event.eventId())`. If `true`, log duplicate warning and acknowledge Kafka offset immediately.
   - Step 2: In a transactional boundary, process downstream domain actions and insert the `ProcessedEvent` record.
   - Step 3: If concurrent race conditions attempt to insert the same `event_id`, database throws `DataIntegrityViolationException`, preventing double processing.
