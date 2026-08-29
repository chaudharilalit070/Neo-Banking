# Customer Microservice — API Testing Guide & Test Evidence

## Automated Test Coverage Summary

All REST endpoints, security filters, data validation rules, database transactions, outbox publication events, and idempotent consumers are tested using JUnit 5, MockMvc, and Spring Boot Test.

| Test Class | Package | Level | Scenarios Tested | Pass Rate |
|---|---|---|---|---|
| `CustomerControllerApiTest` | `com.neobank.neobank_backend.api` | Level 3 (Controller) | 201 Created, 400 Bad Request (Validation), 200 OK Read, 404 Not Found, 200 OK Update | **100% (5/5)** |
| `CustomerLifecycleControllerApiTest` | `com.neobank.neobank_backend.api` | Level 3 (Controller) | Apply Valid Action, Invalid Transition 400, Get Current 200, Get History 200, 404 Not Found | **100% (5/5)** |
| `CustomerContactControllerApiTest` | `com.neobank.neobank_backend.api` | Level 3 (Controller) | Add Contact, Get Contacts, Verify Contact, Set Primary, Deactivate, Duplicate 409 Conflict | **100% (2/2)** |
| `CustomerAddressControllerApiTest` | `com.neobank.neobank_backend.api` | Level 3 (Controller) | Add Address, List Addresses, Get Address By ID, Update Address, Deactivate Address | **100% (1/1)** |
| `CustomerPreferenceControllerApiTest`| `com.neobank.neobank_backend.api` | Level 3 (Controller) | Create Prefs, Get Prefs, Update Prefs, Deactivate Prefs | **100% (1/1)** |
| `CustomerConsentControllerApiTest` | `com.neobank.neobank_backend.api` | Level 3 (Controller) | Grant Consent, Get History, Get by Type, Get Latest, Withdraw Consent | **100% (1/1)** |
| `AuditControllerApiTest` | `com.neobank.neobank_backend.api` | Level 3 (Controller) | Query Audit History by Customer ID with correlation details | **100% (1/1)** |
| `SecurityAuthorizationTest` | `com.neobank.neobank_backend.security` | Level 5 (Security) | 401 Unauthorized JSON, 403 Forbidden JSON, 200 OK Actuator Health, Role authorization | **100% (4/4)** |
| `CustomerFullJourneyIntegrationTest` | `com.neobank.neobank_backend.journey` | Level 8 (E2E Journey) | Complete 13-step customer journey from PROSPECT to CLOSED with Audit and Outbox validation | **100% (1/1)** |

---

## Example cURL Execution Commands

### 1. Create Customer
```bash
curl -X POST "http://localhost:8081/api/v1/customers" \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <JWT_TOKEN>" \
     -H "X-Correlation-Id: eea28ce1-b9ea-4dfc-8db8-1e4337b51b75" \
     -d '{
       "customerType": "INDIVIDUAL",
       "firstName": "Alexander",
       "middleName": "The",
       "lastName": "Great",
       "dateOfBirth": "1990-07-20",
       "nationality": "GRC"
     }'
```

### 2. Transition Lifecycle to ONBOARDING
```bash
curl -X POST "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/lifecycle/actions" \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <JWT_TOKEN>" \
     -d '{
       "action": "START_ONBOARDING"
     }'
```
