# Customer Microservice — Complete REST API Reference

## Base URL & Headers

- **Base Path**: `/api/v1`
- **Standard Request Headers**:
  - `Content-Type: application/json`
  - `Authorization: Bearer <JWT_TOKEN>`
  - `X-Correlation-Id: <UUID>` (Optional, auto-generated if missing)
- **Standard Success Response Envelope**:
  ```json
  {
    "timestamp": "2026-08-27T16:20:00Z",
    "correlationId": "8f39beae-460d-4bd9-974a-47ecb1e5ecfa",
    "data": { ... }
  }
  ```

---

## 1. Customer Profile APIs (`/api/v1/customers`)

### 1.1 Create Customer Profile
- **Method**: `POST /api/v1/customers`
- **Auth Roles**: `ROLE_ADMIN`, `ROLE_OPERATIONS`
- **Request Body**:
  ```json
  {
    "customerType": "INDIVIDUAL",
    "firstName": "John",
    "middleName": "David",
    "lastName": "Doe",
    "dateOfBirth": "1990-05-15",
    "nationality": "USA"
  }
  ```
- **Response (201 Created)**:
  ```json
  {
    "timestamp": "2026-08-27T16:20:00Z",
    "correlationId": "8f39beae-460d-4bd9-974a-47ecb1e5ecfa",
    "data": {
      "id": "e9a073f4-41d6-444f-8f81-2a543ecab716",
      "customerNumber": "CUS-1234567890ABCDEF",
      "customerType": "INDIVIDUAL",
      "customerStatus": "PROSPECT",
      "firstName": "John",
      "middleName": "David",
      "lastName": "Doe",
      "dateOfBirth": "1990-05-15",
      "nationality": "USA",
      "createdAt": "2026-08-27T16:20:00Z",
      "createdBy": "admin-user",
      "updatedAt": null,
      "updatedBy": null
    }
  }
  ```

### 1.2 Get Customer By ID
- **Method**: `GET /api/v1/customers/{customerId}`
- **Auth Roles**: `ROLE_ADMIN`, `ROLE_OPERATIONS`, `ROLE_AUDITOR`
- **Response (200 OK)**: Wrapped in `ApiResponse<CustomerResponse>`.

### 1.3 Update Customer Profile
- **Method**: `PUT /api/v1/customers/{customerId}`
- **Auth Roles**: `ROLE_ADMIN`, `ROLE_OPERATIONS`
- **Request Body**:
  ```json
  {
    "firstName": "Johnny",
    "middleName": "David",
    "lastName": "Doe-Smith",
    "dateOfBirth": "1990-05-15",
    "nationality": "USA"
  }
  ```
- **Response (200 OK)**: Wrapped in `ApiResponse<CustomerResponse>`.

---

## 2. Customer Lifecycle APIs (`/api/v1/customers/{customerId}/lifecycle`)

### 2.1 Apply Lifecycle Action
- **Method**: `POST /api/v1/customers/{customerId}/lifecycle/actions`
- **Auth Roles**: `ROLE_ADMIN`, `ROLE_OPERATIONS`
- **Request Body**:
  ```json
  {
    "action": "START_ONBOARDING"
  }
  ```
  *(Supported actions: `START_ONBOARDING`, `COMPLETE_ONBOARDING`, `DEACTIVATE`, `REACTIVATE`, `CLOSE`)*
- **Response (200 OK)**:
  ```json
  {
    "timestamp": "2026-08-27T16:21:00Z",
    "correlationId": "8f39beae-460d-4bd9-974a-47ecb1e5ecfa",
    "data": {
      "id": 1,
      "customerId": "e9a073f4-41d6-444f-8f81-2a543ecab716",
      "previousStatus": "PROSPECT",
      "currentStatus": "ONBOARDING",
      "reason": "ONBOARDING_STARTED",
      "effectiveAt": "2026-08-27T16:21:00Z"
    }
  }
  ```

### 2.2 Get Current Lifecycle State
- **Method**: `GET /api/v1/customers/{customerId}/lifecycle/current`
- **Auth Roles**: `ROLE_ADMIN`, `ROLE_OPERATIONS`, `ROLE_AUDITOR`
- **Response (200 OK)**: Wrapped in `ApiResponse<CustomerLifecycleResponse>`.

### 2.3 Get Lifecycle History
- **Method**: `GET /api/v1/customers/{customerId}/lifecycle`
- **Auth Roles**: `ROLE_ADMIN`, `ROLE_OPERATIONS`, `ROLE_AUDITOR`
- **Response (200 OK)**: Wrapped in `ApiResponse<List<CustomerLifecycleResponse>>`.

---

## 3. Customer Contact APIs (`/api/v1/customers/{customerId}/contacts`)

### 3.1 Add Customer Contact
- **Method**: `POST /api/v1/customers/{customerId}/contacts`
- **Auth Roles**: `ROLE_ADMIN`, `ROLE_OPERATIONS`
- **Request Body**:
  ```json
  {
    "contactType": "EMAIL",
    "contactValue": "john.doe@company.com",
    "primary": true
  }
  ```
- **Response (201 Created)**: Wrapped in `ApiResponse<CustomerContactResponse>`.

### 3.2 List Customer Contacts
- **Method**: `GET /api/v1/customers/{customerId}/contacts`
- **Auth Roles**: `ROLE_ADMIN`, `ROLE_OPERATIONS`, `ROLE_AUDITOR`
- **Response (200 OK)**: Wrapped in `ApiResponse<List<CustomerContactResponse>>`.

### 3.3 Set Primary Contact
- **Method**: `PATCH /api/v1/customers/{customerId}/contacts/{contactId}/primary`
- **Auth Roles**: `ROLE_ADMIN`, `ROLE_OPERATIONS`

### 3.4 Verify Contact
- **Method**: `PATCH /api/v1/customers/{customerId}/contacts/{contactId}/verify`
- **Auth Roles**: `ROLE_ADMIN`, `ROLE_OPERATIONS`

### 3.5 Deactivate Contact
- **Method**: `PATCH /api/v1/customers/{customerId}/contacts/{contactId}/deactivate`
- **Auth Roles**: `ROLE_ADMIN`, `ROLE_OPERATIONS`

---

## 4. Customer Address APIs (`/api/v1/customers/{customerId}/addresses`)

### 4.1 Add Address
- **Method**: `POST /api/v1/customers/{customerId}/addresses`
- **Auth Roles**: `ROLE_ADMIN`, `ROLE_OPERATIONS`
- **Request Body**:
  ```json
  {
    "addressType": "PERMANENT",
    "addressLine1": "100 Broadway St",
    "addressLine2": "Suite 500",
    "city": "New York",
    "state": "NY",
    "country": "USA",
    "postalCode": "10001"
  }
  ```
- **Response (201 Created)**: Wrapped in `ApiResponse<CustomerAddressResponse>`.

### 4.2 List Addresses
- **Method**: `GET /api/v1/customers/{customerId}/addresses`
- **Auth Roles**: `ROLE_ADMIN`, `ROLE_OPERATIONS`, `ROLE_AUDITOR`

### 4.3 Get Address By ID
- **Method**: `GET /api/v1/customers/{customerId}/addresses/{addressId}`
- **Auth Roles**: `ROLE_ADMIN`, `ROLE_OPERATIONS`, `ROLE_AUDITOR`

### 4.4 Update Address
- **Method**: `PUT /api/v1/customers/{customerId}/addresses/{addressId}`
- **Auth Roles**: `ROLE_ADMIN`, `ROLE_OPERATIONS`

### 4.5 Deactivate Address
- **Method**: `PATCH /api/v1/customers/{customerId}/addresses/{addressId}/deactivate`
- **Auth Roles**: `ROLE_ADMIN`, `ROLE_OPERATIONS`

---

## 5. Customer Preference APIs (`/api/v1/customers/{customerId}/preferences`)

### 5.1 Create Preferences
- **Method**: `POST /api/v1/customers/{customerId}/preferences`
- **Request Body**:
  ```json
  {
    "preferredLanguage": "ENGLISH",
    "preferredCommunicationChannel": "EMAIL",
    "marketingNotifications": false,
    "transactionNotifications": true,
    "securityNotifications": true
  }
  ```

### 5.2 Get Preferences
- **Method**: `GET /api/v1/customers/{customerId}/preferences`

### 5.3 Update Preferences
- **Method**: `PATCH /api/v1/customers/{customerId}/preferences`

### 5.4 Deactivate Preferences
- **Method**: `PATCH /api/v1/customers/{customerId}/preferences/deactivate`

---

## 6. Customer Consent APIs (`/api/v1/customers/{customerId}/consents`)

### 6.1 Grant Consent
- **Method**: `POST /api/v1/customers/{customerId}/consents`
- **Request Body**:
  ```json
  {
    "consentType": "TERMS_AND_CONDITIONS",
    "consentVersion": "v2.0",
    "consentTextVersion": "v2.0-terms",
    "source": "WEB"
  }
  ```

### 6.2 Withdraw Consent
- **Method**: `PATCH /api/v1/customers/{customerId}/consents/withdraw`
- **Request Body**:
  ```json
  {
    "consentType": "TERMS_AND_CONDITIONS",
    "source": "WEB"
  }
  ```

### 6.3 Get Consent History
- **Method**: `GET /api/v1/customers/{customerId}/consents`

### 6.4 Get Consent History By Type
- **Method**: `GET /api/v1/customers/{customerId}/consents/{consentType}`

### 6.5 Get Latest Consent By Type
- **Method**: `GET /api/v1/customers/{customerId}/consents/{consentType}/latest`

---

## 7. Audit APIs (`/api/v1/customers/{customerId}/audit`)

### 7.1 Get Customer Audit Trail
- **Method**: `GET /api/v1/customers/{customerId}/audit?from=2026-08-01T00:00:00&to=2026-08-28T00:00:00`
- **Auth Roles**: `ROLE_ADMIN`, `ROLE_AUDITOR`
- **Response (200 OK)**:
  ```json
  {
    "timestamp": "2026-08-27T16:25:00Z",
    "correlationId": "8f39beae-460d-4bd9-974a-47ecb1e5ecfa",
    "data": [
      {
        "id": 1,
        "customerId": "e9a073f4-41d6-444f-8f81-2a543ecab716",
        "action": "CUSTOMER_CREATED",
        "previousState": null,
        "newState": "PROSPECT",
        "description": "Initial Creation",
        "actorId": "admin-user",
        "actorType": "EMPLOYEE",
        "correlationId": "8f39beae-460d-4bd9-974a-47ecb1e5ecfa",
        "timestamp": "2026-08-27T16:20:00Z",
        "sourceService": "customer-service"
      }
    ]
  }
  ```
