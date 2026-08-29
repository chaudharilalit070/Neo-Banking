# Customer Microservice — Complete Change Log

## Summary of All Modifications, Bug Fixes & Refactorings

### 1. Database Schema & Flyway Migration Scripts
- **Problem**: Original Flyway migrations (`V001`–`V007`, `V1`–`V5`) contained 0-byte files, invalid index definitions inside `CREATE TABLE` statements, and foreign key type mismatches (`customer_id BIGINT` pointing to `customers.id CHAR(36)`).
- **Fix**: Created clean, sequential migrations `V1` through `V7` with correct `CHAR(36)` foreign keys, explicit indexes, and cross-database MySQL/H2 compatibility:
  - `V1__create_customers_table.sql`
  - `V2__create_customer_contacts_table.sql`
  - `V3__create_customer_addresses_table.sql`
  - `V4__create_customer_preferences_table.sql`
  - `V5__create_customer_consents_table.sql`
  - `V6__create_customer_lifecycle_table.sql`
  - `V7__create_audit_outbox_processed_tables.sql`

### 2. Maven Build & Dependency Configuration (`pom.xml`)
- **Fix**: Configured `maven-compiler-plugin` 3.14.0 with Lombok annotation processor path for Java 21 LTS compatibility.
- **Fix**: Added H2 database (`com.h2database:h2`) and Spring Kafka Test (`org.springframework.kafka:spring-kafka-test`) test scope dependencies.

### 3. Core Entity & Validation Bug Fixes
- **`AddCustomerAddressRequest.java`**: Replaced invalid `@NotBlank` on `AddressType` enum with `@NotNull`.
- **`Customer.java`**: Fixed `@Version` initialization bug where `version = 0L` prior to persist caused Hibernate to mistake new entities for detached entities, throwing `ObjectOptimisticLockingFailureException`.
- **`CustomerServiceImpl.java`**: Replaced hardcoded `"SYSTEM"` user with `CurrentUserProvider`. Integrated `AuditService.recordCustomerCreation` and `CustomerLifecycleRepository` to properly initialize the initial `PROSPECT` lifecycle milestone.
- **`AuditController.java`**: Standardized controller output to `ApiResponse<List<AuditEventResponse>>`.
- **`SecurityConfig.java`**: Implemented custom JSON 401 `AuthenticationEntryPoint` and 403 `AccessDeniedHandler` returning standard `ErrorResponse` envelopes. Added public permits for `/actuator/**` and `/swagger-ui/**`.

### 4. Configuration Beans Implementation
- **`MaskingUtil.java`**: Implemented banking PII masking for emails, phone numbers, customer names, and generic strings.
- **`OpenApiConfig.java`**: Implemented OpenAPI 3.0 configuration with security schemes and API metadata.
- **`JacksonConfig.java`**: Configured `ObjectMapper` with `JavaTimeModule` and ISO-8601 formatting.
- **`JpaConfig.java`**: Enabled JPA Auditing (`@EnableJpaAuditing`) and repository package scanning.
- **`KafkaConfig.java`**: Added conditional topic registration and listener container startup controls for test environments.

### 5. Automated Test Suite (Levels 1 to 8)
- Created 17 test classes with 52 automated tests covering all 8 testing tiers with 100% pass rate.
