# Customer Microservice — System Architecture

## Architecture Pattern: Hexagonal / Clean Architecture

The Customer Microservice follows Hexagonal (Ports and Adapters) and Clean Architecture principles to decouple the core banking domain models and business policies from framework-specific infrastructure, persistence, and external communication channels.

```
                    ┌───────────────────────────────────────────────┐
                    │               Inbound Adapters                │
                    │   (REST Controllers, Kafka Message Consumers) │
                    └───────────────────────┬───────────────────────┘
                                            │
                                            ▼
                    ┌───────────────────────────────────────────────┐
                    │              Application Layer                │
                    │   (Services, Use Cases, Transaction Boundary) │
                    └───────────────────────┬───────────────────────┘
                                            │
                                            ▼
                    ┌───────────────────────────────────────────────┐
                    │                 Domain Layer                  │
                    │   (Entities, Aggregates, State Transition     │
                    │    Policies, Domain Events, Repositories)     │
                    └───────────────────────┬───────────────────────┘
                                            │
                                            ▼
                    ┌───────────────────────────────────────────────┐
                    │               Outbound Adapters               │
                    │   (JPA Repositories, Kafka Producer, Outbox)  │
                    └───────────────────────────────────────────────┘
```

---

## Package Hierarchy & Component Responsibilities

```
com.neobank.neobank_backend
├── address
│   ├── api (Controllers, Requests, Responses)
│   ├── application (CustomerAddressService, Implementation)
│   ├── domain (CustomerAddress entity, AddressType, AddressStatus, CustomerAddressRepository port)
│   └── infrastructure (JpaCustomerAddressRepository, AddressRepositoryAdapter)
├── audit
│   ├── api (AuditController, AuditEventResponse)
│   ├── application (AuditService, Implementation)
│   ├── domain (AuditEvent entity, AuditAction, ActorType, AuditEventRepository port)
│   └── infrastructure (JpaAuditEventRepository, AuditEventRepositoryAdapter)
├── common
│   ├── constants (ErrorCodes, SecurityConstants)
│   ├── exception (BusinessException, ResourceNotFoundException, ConflictException, GlobalExceptionHandler)
│   ├── security (CurrentUserProvider, SecurityContextUserProvider)
│   ├── util (MaskingUtil)
│   └── web (ApiResponse, ErrorResponse, CorrelationIdFilter)
├── config (SecurityConfig, OpenApiConfig, JacksonConfig, JpaConfig, KafkaConfig)
├── consent
│   ├── api, application, domain, infrastructure (CustomerConsent aggregate, types, sources)
├── contact
│   ├── api, application, domain, infrastructure (CustomerContact aggregate, contact types)
├── customer
│   ├── api (CustomerController, Create/Update Customer requests & responses)
│   ├── application (CustomerService, Implementation, DefaultCustomerNumberGenerator)
│   ├── domain (Customer entity, CustomerStatus, CustomerType, CustomerRepository port)
│   └── infrastructure (JpaCustomerRepository, CustomerRepositoryAdapter)
├── lifecycle
│   ├── api (CustomerLifecycleController, Action requests & responses)
│   ├── application (CustomerLifecycleService, Implementation)
│   ├── domain (CustomerLifecycle entity, Statuses, Actions, Reasons, TransitionPolicy, Repository port)
│   ├── event (CustomerLifecycleChangedEvent, OutboxEvent, ProcessedEvent)
│   └── infrastructure (Outbox publisher scheduler, Idempotent event consumer, JPA adapters)
└── preference
    ├── api, application, domain, infrastructure (CustomerPreference aggregate, language & channels)
```

---

## Technical Constraints & Design Decisions

1. **UUID Primary Keys**: Customers are identified by RFC 4122 UUIDs stored in the database as `CHAR(36)` columns to ensure distributed uniqueness and prevent sequential enumeration attacks.
2. **Deterministic Business Identifier**: A public `customer_number` (`CUS-` prefix + 16 alphanumeric characters) is generated for external banking displays, receipts, and search operations.
3. **Pessimistic vs Optimistic Locking**:
   - `Customer` entity uses `@Version` column for optimistic locking protection during concurrent profile updates.
   - Lifecycle transitions are guarded by atomic database transactions.
4. **Audit Trail Completeness**: Every state change (creation, profile update, lifecycle transition) automatically generates an immutable audit record in the `audit_events` table before committing.
5. **PII Masking**: Sensitive customer identifiers (emails, phone numbers, tax IDs) are masked across operational logs and non-privileged interfaces using `MaskingUtil`.
