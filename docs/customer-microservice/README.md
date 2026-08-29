# Customer Microservice — Production Documentation

## Overview

The **Customer Microservice** is the core identity and lifecycle backbone of the Neo-Banking platform. It manages customer master profiles, contact vectors (email, mobile), residential and permanent physical addresses, communication preferences, regulatory consents (GDPR, Data Processing, Terms & Conditions), state machine lifecycle transitions (`PROSPECT` $\rightarrow$ `ONBOARDING` $\rightarrow$ `ACTIVE` $\rightleftharpoons$ `INACTIVE` $\rightarrow$ `CLOSED`), transactional outbox event publishing, idempotent consumer message processing, and comprehensive immutable audit logging.

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          NEO-BANKING PLATFORM ECOSYSTEM                         │
└─────────────────────────────────────────────────────────────────────────────────┘
                                         │
                 ┌───────────────────────┴───────────────────────┐
                 ▼                                               ▼
     ┌───────────────────────┐                       ┌───────────────────────┐
     │  REST API Consumers   │                       │   Event Consumers     │
     │  (Web, Mobile, Ops)   │                       │  (Account, Card, KYC) │
     └───────────┬───────────┘                       └───────────▲───────────┘
                 │ (HTTP / JSON / JWT)                           │
                 ▼                                               │ (Kafka)
┌────────────────────────────────────────────────────────────────┴────────────────┐
│                           CUSTOMER MICROSERVICE (8081)                         │
│                                                                                 │
│  ┌─────────────────────────┐  ┌─────────────────────────┐  ┌─────────────────┐  │
│  │   Customer Profile      │  │    Contact & Address    │  │ Preference &    │  │
│  │   Aggregate             │  │    Sub-domains          │  │ Consent         │  │
│  └────────────┬────────────┘  └────────────┬────────────┘  └────────┬────────┘  │
│               │                            │                        │           │
│  ┌────────────┴────────────┐  ┌────────────┴────────────┐           │           │
│  │    Lifecycle State      │  │     Immutable Audit     │           │           │
│  │    Machine Policy       │  │     Log Engine          │           │           │
│  └────────────┬────────────┘  └────────────┬────────────┘           │           │
│               │                            │                        │           │
│  ┌────────────┴────────────────────────────┴────────────────────────┴────────┐  │
│  │                   Transactional Outbox Publisher Engine                   │  │
│  └─────────────────────────────────────────┬─────────────────────────────────┘  │
└────────────────────────────────────────────┼────────────────────────────────────┘
                                             │ (Writes & Reads)
                                             ▼
                             ┌───────────────────────────────┐
                             │       MySQL Database          │
                             │  (InnoDB / CHAR(36) UUIDs)    │
                             └───────────────────────────────┘
```

---

## Directory Navigation

- [1. System Architecture](architecture/architecture.md)
- [2. Customer Journey Flow](architecture/customer-flow.md)
- [3. Lifecycle State Machine Flow](architecture/lifecycle-flow.md)
- [4. Security & Role Flow](architecture/security-flow.md)
- [5. Audit & Compliance Flow](architecture/audit-flow.md)
- [6. Kafka & Outbox Flow](architecture/kafka-flow.md)
- [7. Complete REST API Reference](api/api-documentation.md)
- [8. API Testing Evidence](api/api-testing.md)
- [9. Database Architecture & Schema](database/database-documentation.md)
- [10. Security Architecture](security/security-documentation.md)
- [11. Event Contracts](events/event-contracts.md)
- [12. Transactional Outbox Pattern](events/outbox.md)
- [13. Idempotent Consumer Pattern](events/idempotency.md)
- [14. Comprehensive Test Report](testing/test-report.md)
- [15. Complete Change Log](changes/change-log.md)

---

## Key Tech Stack Specifications

| Layer | Technology |
|---|---|
| **Runtime** | Java 21 LTS |
| **Framework** | Spring Boot 3.5.0, Spring Security 6, Spring Data JPA |
| **Database** | MySQL 8.0+ / H2 In-Memory (Test Mode) |
| **Migration** | Flyway 11 (Strict Sequential `V1` – `V7`) |
| **Messaging** | Apache Kafka (Spring Kafka) |
| **Documentation** | OpenAPI 3.0 / Swagger UI (`/swagger-ui.html`) |
| **Metrics & Health** | Spring Boot Actuator (`/actuator/health`, `/actuator/info`) |
