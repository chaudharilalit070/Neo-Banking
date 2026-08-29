# Transactional Outbox Pattern Architecture

## Problem Statement

In distributed systems, publishing an event directly to a message broker (Kafka) inside a database transaction risks the Dual-Write Problem:
1. Database commits, but network failure prevents Kafka publish $\rightarrow$ Event is lost.
2. Kafka publish succeeds, but database transaction rolls back $\rightarrow$ Phantom event is published.

---

## Solution: Transactional Outbox Pattern

```mermaid
sequenceDiagram
    autonumber
    participant App as CustomerLifecycleService
    participant DB as MySQL DB
    participant Outbox as Outbox Table
    participant Poller as CustomerLifecycleOutboxPublisher
    participant Kafka as Kafka Broker

    Note over App, Outbox: Phase 1: Local Transaction
    App->>DB: UPDATE customers SET customer_status = 'ACTIVE'
    App->>DB: INSERT INTO customer_lifecycle (...)
    App->>DB: INSERT INTO audit_events (...)
    App->>Outbox: INSERT INTO event_outbox (status = 'PENDING', payload = JSON)
    App->>DB: COMMIT TRANSACTION

    Note over Poller, Kafka: Phase 2: Asynchronous Outbox Polling
    loop Every 5000ms
        Poller->>Outbox: SELECT * FROM event_outbox WHERE status = 'PENDING' LIMIT 100
        Poller->>Kafka: KafkaTemplate.send("customer.lifecycle.changed", key, payload)
        alt Kafka Send Success
            Poller->>Outbox: UPDATE event_outbox SET status = 'PUBLISHED', published_at = NOW()
        else Kafka Send Failure
            Poller->>Outbox: UPDATE event_outbox SET status = 'FAILED', retry_count = retry_count + 1
        end
    end
```

---

## Key Resilience Features

- **No Distributed Transactions (2PC)**: Keeps the operational database fast and independent of broker availability.
- **Ordered Polling**: `findTop100ByStatusOrderByCreatedAtAsc` preserves message order per partition key.
- **Fail-Safe Retries**: Transient Kafka outages simply leave records in `PENDING`/`FAILED` state until connectivity resumes.
