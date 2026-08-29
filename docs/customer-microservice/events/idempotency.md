# Idempotent Consumer & Deduplication Architecture

## The Duplicate Message Problem

Kafka guarantees **At-Least-Once Delivery**. During consumer group rebalances, network timeouts, or retried offset commits, the consumer may receive the exact same event payload multiple times.

---

## The Idempotent Consumer Solution

```mermaid
sequenceDiagram
    autonumber
    participant Kafka as Kafka Broker
    participant Consumer as CustomerLifecycleEventConsumer
    participant Repo as ProcessedEventRepository
    participant DB as MySQL DB (processed_event)

    Kafka->>Consumer: Deliver message with eventId = "b6a8d6f0-..."
    Consumer->>Repo: existsByEventId("b6a8d6f0-...")
    alt Event Already Processed
        Repo-->>Consumer: true
        Note over Consumer: Log duplicate skip warning; ACK Kafka offset
    else New Event
        Repo-->>Consumer: false
        Consumer->>Consumer: Execute business processing logic
        Consumer->>Repo: save(ProcessedEvent: eventId="b6a8d6f0-...", eventType="...")
        Repo->>DB: INSERT INTO processed_event (enforces UK uk_processed_event_event_id)
        Note over Consumer: ACK Kafka offset
    end
```

---

## Database Constraint Safety

The `processed_event` table includes a unique constraint on `event_id`:
```sql
CONSTRAINT uk_processed_event_event_id UNIQUE (event_id)
```
If two consumer threads or parallel instances attempt to process the same `event_id` simultaneously, the first insert commits and the second fails with `DataIntegrityViolationException`, preventing double processing.
