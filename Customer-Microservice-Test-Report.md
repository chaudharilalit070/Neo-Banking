# Customer Microservice — Comprehensive Test Report

## Summary

- **Total Test Classes**: 17
- **Total Tests Run**: 52
- **Passed**: 52 (100.0%)
- **Failures**: 0
- **Errors**: 0
- **Skipped**: 0
- **Execution Command**: `.\mvnw.cmd clean test`
- **Execution Duration**: 01:11 min
- **Build Status**: **SUCCESS**

---

## Testing Matrix by Tier

| Tier | Category | Test Class | Methods | Status |
|---|---|---|---|---|
| **Level 1** | Unit / Policy | `CustomerLifecycleTransitionPolicyTest` | 8 | PASSED |
| **Level 1** | Unit / Domain | `CustomerTest` | 3 | PASSED |
| **Level 1** | Unit / Generator | `DefaultCustomerNumberGeneratorTest` | 2 | PASSED |
| **Level 1** | Unit / Masking | `MaskingUtilTest` | 4 | PASSED |
| **Level 2** | Repository | `CustomerRepositoryTest` | 1 | PASSED |
| **Level 2** | Repository | `CustomerLifecycleRepositoryTest` | 1 | PASSED |
| **Level 2** | Repository | `CustomerContactRepositoryTest` | 1 | PASSED |
| **Level 2** | Repository | `CustomerAddressRepositoryTest` | 1 | PASSED |
| **Level 2** | Repository | `CustomerPreferenceRepositoryTest` | 1 | PASSED |
| **Level 2** | Repository | `CustomerConsentRepositoryTest` | 1 | PASSED |
| **Level 2** | Repository | `AuditEventRepositoryTest` | 1 | PASSED |
| **Level 2** | Repository | `OutboxEventRepositoryTest` | 1 | PASSED |
| **Level 2** | Repository | `ProcessedEventRepositoryTest` | 1 | PASSED |
| **Level 3** | Controller / API | `CustomerControllerApiTest` | 5 | PASSED |
| **Level 3** | Controller / API | `CustomerLifecycleControllerApiTest` | 5 | PASSED |
| **Level 3** | Controller / API | `CustomerContactControllerApiTest` | 2 | PASSED |
| **Level 3** | Controller / API | `CustomerAddressControllerApiTest` | 1 | PASSED |
| **Level 3** | Controller / API | `CustomerPreferenceControllerApiTest`| 1 | PASSED |
| **Level 3** | Controller / API | `CustomerConsentControllerApiTest` | 1 | PASSED |
| **Level 3** | Controller / API | `AuditControllerApiTest` | 1 | PASSED |
| **Level 5** | Security / RBAC | `SecurityAuthorizationTest` | 4 | PASSED |
| **Level 6** | Transaction Rollback| `CustomerTransactionRollbackTest` | 1 | PASSED |
| **Level 7** | Kafka Outbox | `CustomerLifecycleOutboxPublisherTest` | 2 | PASSED |
| **Level 7** | Kafka Idempotency | `CustomerLifecycleConsumerIdempotencyTest`| 1 | PASSED |
| **Level 8** | E2E Full Journey | `CustomerFullJourneyIntegrationTest` | 1 | PASSED |
| **Context** | Spring Boot App | `NeobankBackendApplicationTests` | 1 | PASSED |
