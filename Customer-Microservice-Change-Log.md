# Customer Microservice — Complete Change Log

## Overview of System Fixes & Improvements

### 1. Database Schema & Flyway Migration Consolidation
- **Identified Issues**: Multiple conflicting, empty, and broken SQL scripts in `src/main/resources/db/migration/`. Foreign keys were typed as `customer_id BIGINT` while `customers.id` was defined as `CHAR(36) UUID`. Invalid `KEY` statements inside `CREATE TABLE` caused MySQL/H2 syntax errors.
- **Resolution**: Removed obsolete files and authored sequential migrations `V1` through `V7`:
  - `V1__create_customers_table.sql`: Customer master table with status indexes and unique `customer_number`.
  - `V2__create_customer_contacts_table.sql`: `customer_contacts` with `CHAR(36)` FK and unique `(customer_id, contact_type, contact_value)`.
  - `V3__create_customer_addresses_table.sql`: `customer_addresses` with `CHAR(36)` FK and unique `(customer_id, address_type)`.
  - `V4__create_customer_preferences_table.sql`: `customer_preferences` with `CHAR(36)` FK and unique constraint on `customer_id`.
  - `V5__create_customer_consents_table.sql`: `customer_consents` with `CHAR(36)` FK and timestamp indexes.
  - `V6__create_customer_lifecycle_table.sql`: `customer_lifecycle` history log with `CHAR(36)` FK.
  - `V7__create_audit_outbox_processed_tables.sql`: `audit_events`, `event_outbox`, and `processed_event` with unique index `uk_processed_event_event_id`.

### 2. Maven Build & Compiler Fixes
- Added `annotationProcessorPaths` for Lombok in `pom.xml` to fix Maven compilation under Java 21 LTS and Spring Boot 3.5.0.
- Added test scope dependencies: `com.h2database:h2` and `org.springframework.kafka:spring-kafka-test`.

### 3. Core Business Logic & Entity Fixes
- **`AddCustomerAddressRequest.java`**: Replaced invalid `@NotBlank` on enum `AddressType` with `@NotNull`.
- **`Customer.java`**: Fixed Hibernate optimistic locking bug where `version = 0L` prior to persist prevented new entity insertion.
- **`CustomerServiceImpl.java`**: Integrated `CurrentUserProvider`, `AuditService`, and `CustomerLifecycleRepository` to properly initialize audit and lifecycle state on prospect creation.
- **`AuditController.java`**: Standardized controller output to `ApiResponse<List<AuditEventResponse>>`.
- **`SecurityConfig.java`**: Added custom JSON 401 `AuthenticationEntryPoint` and 403 `AccessDeniedHandler` with standard `ErrorResponse` formatting.

### 4. Infrastructure & Configuration
- **`MaskingUtil.java`**: Implemented banking PII masking for emails, phone numbers, names, and generic strings.
- **`OpenApiConfig.java`**: Configured OpenAPI 3.0 metadata and Bearer JWT security scheme.
- **`JacksonConfig.java`**: Configured `ObjectMapper` with JavaTimeModule and ISO-8601 formatting.
- **`JpaConfig.java`**: Configured JPA Auditing and repository scanning.
- **`KafkaConfig.java`**: Made topic creation conditional and disabled consumer auto-startup during test profiles.

### 5. Multi-Level Testing Suite
- Implemented 17 test classes with 52 automated tests across all 8 tiers (Unit, Repository, Controller, Integration, Security, Rollback, Messaging, E2E Journey) with 100% pass rate.
