# Customer Lifecycle Event Contracts

## Topic Specification

- **Topic Name**: `customer.lifecycle.changed`
- **Partition Key**: `customerId` (UUID String)
- **Serialization**: JSON String (`org.apache.kafka.common.serialization.StringSerializer`)

---

## Schema & JSON Contract

```json
{
  "eventId": "b6a8d6f0-50d4-4bc7-9db6-e41c47ea4e70",
  "eventType": "customer.lifecycle.changed",
  "schemaVersion": 1,
  "occurredAt": "2026-08-27T16:21:00.123456",
  "source": "customer-service",
  "correlationId": "8f39beae-460d-4bd9-974a-47ecb1e5ecfa",
  "customerId": "e9a073f4-41d6-444f-8f81-2a543ecab716",
  "data": {
    "lifecycleId": 12,
    "previousStatus": "PROSPECT",
    "currentStatus": "ONBOARDING",
    "reason": "ONBOARDING_STARTED",
    "effectiveAt": "2026-08-27T16:21:00.123456"
  }
}
```

---

## Field Dictionary

| Field | Type | Description |
|---|---|---|
| `eventId` | UUID String | Globally unique identifier for this event instance (used for consumer idempotency). |
| `eventType` | String | Event type discriminator (`customer.lifecycle.changed`). |
| `schemaVersion` | Integer | Event schema version number (currently `1`). |
| `occurredAt` | ISO-8601 Timestamp | Timestamp when the domain state change was committed. |
| `source` | String | Originating microservice (`customer-service`). |
| `correlationId` | String | Distributed tracing correlation ID. |
| `customerId` | UUID String | Aggregate Root Customer UUID. |
| `data.lifecycleId` | Long | Database ID of the created `customer_lifecycle` entry. |
| `data.previousStatus`| String | Customer lifecycle status before transition (`PROSPECT`, `ONBOARDING`, `ACTIVE`, `INACTIVE`). |
| `data.currentStatus` | String | Customer lifecycle status after transition (`ONBOARDING`, `ACTIVE`, `INACTIVE`, `CLOSED`). |
| `data.reason` | String | Reason code for the transition (`ONBOARDING_STARTED`, `ONBOARDING_COMPLETED`, `CUSTOMER_DEACTIVATED`, `CUSTOMER_REACTIVATED`, `CUSTOMER_CLOSED`). |
| `data.effectiveAt` | ISO-8601 Timestamp | Effective date/time of the status change. |
