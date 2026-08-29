# Customer Journey & Onboarding Flow

## Customer End-to-End Onboarding Sequence

```mermaid
sequenceDiagram
    autonumber
    actor Client as Web/Mobile App
    participant Gateway as API Gateway
    participant CC as CustomerController
    participant CS as CustomerService
    participant AS as AuditService
    participant Repo as CustomerRepository
    participant DB as MySQL DB

    Client->>Gateway: POST /api/v1/customers (Create Profile)
    Gateway->>CC: Forward Request with JWT & CorrelationId
    CC->>CS: createCustomer(request)
    CS->>CS: Generate customerNumber (CUS-XXXX)
    CS->>Repo: save(Customer: status=PROSPECT)
    Repo->>DB: INSERT INTO customers
    CS->>AS: recordCustomerCreation(customer)
    AS->>DB: INSERT INTO audit_events (action=CUSTOMER_CREATED)
    CS-->>CC: CustomerResponse (status=PROSPECT)
    CC-->>Client: 201 Created (ApiResponse<CustomerResponse>)

    Note over Client, DB: Step 2: Contact, Address, Preferences, Consent Setup
    Client->>Gateway: POST /api/v1/customers/{id}/contacts (Email/Phone)
    Client->>Gateway: POST /api/v1/customers/{id}/addresses (Residential)
    Client->>Gateway: POST /api/v1/customers/{id}/preferences (Language, Channels)
    Client->>Gateway: POST /api/v1/customers/{id}/consents (Terms, Privacy)

    Note over Client, DB: Step 3: Lifecycle Activation
    Client->>Gateway: POST /api/v1/customers/{id}/lifecycle/actions (START_ONBOARDING)
    Gateway->>DB: Update status to ONBOARDING, write outbox event
    Client->>Gateway: POST /api/v1/customers/{id}/lifecycle/actions (COMPLETE_ONBOARDING)
    Gateway->>DB: Update status to ACTIVE, write outbox event
```

---

## Detailed Step-by-Step Breakdown

1. **Prospect Creation**:
   - Customer profile is initialized with first name, last name, date of birth, nationality, and customer type.
   - Initial state is set to `PROSPECT`.
   - Initial `CUSTOMER_CREATED` audit event and `PROSPECT` lifecycle milestone are committed in the same database transaction.
2. **Contact & Verification Setup**:
   - Multiple contact channels (Primary Email, Mobile Phone) are registered.
   - Verification flows (e.g., OTP or email verification) trigger status changes from `PENDING` $\rightarrow$ `VERIFIED`.
3. **Address Registration**:
   - Primary `PERMANENT` or `CURRENT` physical address is saved with country, state, postal code, and lines.
4. **Preferences & Consents**:
   - User language preference (English, Hindi, Marathi) and notifications (Email, SMS, Push) are recorded.
   - Legal consents (Terms and Conditions, Data Processing, Privacy Policy) are recorded with explicit versions and timestamps.
5. **Lifecycle Progression**:
   - Operational/KYC checks initiate `START_ONBOARDING` transitioning the customer to `ONBOARDING`.
   - Once all identity requirements are met, `COMPLETE_ONBOARDING` transitions the customer to `ACTIVE`.
