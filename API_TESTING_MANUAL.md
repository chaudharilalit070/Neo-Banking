# NeoBank Customer Microservice — API Testing Manual & Dummy Data Guide

This document provides a **complete, module-wise API testing specification** for the NeoBank Customer Management Microservice. It details full endpoint URLs, HTTP methods, authorization credentials, valid dummy request payloads, negative test scenarios, expected responses, and ready-to-run cURL commands.

---

## 1. Environment & Global Configuration

### Base Server URL
```
http://localhost:8081
```

### Pre-Configured Test Accounts (HTTP Basic Auth)

| Username | Password | Roles | Access Scope |
| :--- | :--- | :--- | :--- |
| `admin` | `Admin@123` | `ROLE_ADMIN`, `ROLE_OPERATIONS`, `ROLE_AUDITOR` | Full access to all endpoints (Read, Write, Lifecycle, Audit) |
| `ops` | `Ops@123` | `ROLE_OPERATIONS` | Read & Write access (Customer, Contact, Address, Prefs, Lifecycle) |
| `auditor` | `Auditor@123` | `ROLE_AUDITOR` | Read-only access (Customer, Audit logs, Lifecycle history) |

### Global Request Headers

| Header | Required | Example / Description |
| :--- | :---: | :--- |
| `Content-Type` | **Yes** (POST/PUT/PATCH) | `application/json` |
| `Authorization` | **Yes** (All `/api/v1/**`) | `Basic YWRtaW46QWRtaW5AMTIz` (admin) or HTTP Basic via client tool |
| `X-Correlation-Id` | Optional | `9c8a6f44-1234-4bc1-a789-9876543210ab` (Auto-generated UUID if omitted) |

### Standard Response Structure

```json
{
  "timestamp": "2026-08-28T14:30:00Z",
  "correlationId": "9c8a6f44-1234-4bc1-a789-9876543210ab",
  "data": { ... }
}
```

---

## 2. Test Customer UUID for Variable Substitution

When executing chained tests, replace `:customerId`, `:contactId`, and `:addressId` with the actual IDs generated from earlier calls.

> **Default Sample UUID**: `e9a073f4-41d6-444f-8f81-2a543ecab716`

---

## 3. Module 1: Customer Profile Management

### 1.1 Create Customer
* **HTTP Method**: `POST`
* **Full URL**: `http://localhost:8081/api/v1/customers`
* **Access Roles**: `ADMIN`, `OPERATIONS`
* **Description**: Registers a new customer in `PROSPECT` status.

#### Request Headers
```http
Content-Type: application/json
Authorization: Basic YWRtaW46QWRtaW5AMTIz
X-Correlation-Id: 550e8400-e29b-41d4-a716-446655440000
```

#### Valid Dummy Request Body
```json
{
  "customerType": "INDIVIDUAL",
  "firstName": "Aarav",
  "middleName": "Rajesh",
  "lastName": "Sharma",
  "dateOfBirth": "1994-08-15",
  "nationality": "IND"
}
```

#### Alternative Dummy Data (Business Entity)
```json
{
  "customerType": "BUSINESS",
  "firstName": "Apex",
  "middleName": "FinTech",
  "lastName": "Ventures",
  "dateOfBirth": "2015-03-22",
  "nationality": "USA"
}
```

#### Expected Response (`201 Created`)
```json
{
  "timestamp": "2026-08-28T14:30:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "data": {
    "id": "e9a073f4-41d6-444f-8f81-2a543ecab716",
    "customerNumber": "CUS-3B9FA4721C8D",
    "customerType": "INDIVIDUAL",
    "customerStatus": "PROSPECT",
    "firstName": "Aarav",
    "middleName": "Rajesh",
    "lastName": "Sharma",
    "dateOfBirth": "1994-08-15",
    "nationality": "IND",
    "createdAt": "2026-08-28T14:30:00Z",
    "updatedAt": null
  }
}
```

#### cURL Command
```bash
curl -X POST "http://localhost:8081/api/v1/customers" \
  -u "admin:Admin@123" \
  -H "Content-Type: application/json" \
  -H "X-Correlation-Id: 550e8400-e29b-41d4-a716-446655440000" \
  -d '{
    "customerType": "INDIVIDUAL",
    "firstName": "Aarav",
    "middleName": "Rajesh",
    "lastName": "Sharma",
    "dateOfBirth": "1994-08-15",
    "nationality": "IND"
  }'
```

#### Negative Test Scenario: Validation Error (`400 Bad Request`)
* **Payload with Future Date & Missing Last Name**:
```json
{
  "customerType": "INDIVIDUAL",
  "firstName": "Aarav",
  "dateOfBirth": "2030-01-01",
  "nationality": "INVALID"
}
```

---

### 1.2 Get Customer By ID
* **HTTP Method**: `GET`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716`
* **Access Roles**: `ADMIN`, `OPERATIONS`, `AUDITOR`
* **Description**: Fetches profile data of a specific customer.

#### Expected Response (`200 OK`)
```json
{
  "timestamp": "2026-08-28T14:32:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "data": {
    "id": "e9a073f4-41d6-444f-8f81-2a543ecab716",
    "customerNumber": "CUS-3B9FA4721C8D",
    "customerType": "INDIVIDUAL",
    "customerStatus": "PROSPECT",
    "firstName": "Aarav",
    "middleName": "Rajesh",
    "lastName": "Sharma",
    "dateOfBirth": "1994-08-15",
    "nationality": "IND",
    "createdAt": "2026-08-28T14:30:00Z",
    "updatedAt": null
  }
}
```

#### cURL Command
```bash
curl -X GET "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716" \
  -u "auditor:Auditor@123"
```

---

### 1.3 Update Customer Profile
* **HTTP Method**: `PUT`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716`
* **Access Roles**: `ADMIN`, `OPERATIONS`
* **Description**: Updates profile details for an existing customer.

#### Valid Dummy Request Body
```json
{
  "firstName": "Aarav",
  "middleName": "R.",
  "lastName": "Sharma-Patel",
  "dateOfBirth": "1994-08-15",
  "nationality": "IND"
}
```

#### Expected Response (`200 OK`)
```json
{
  "timestamp": "2026-08-28T14:35:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "data": {
    "id": "e9a073f4-41d6-444f-8f81-2a543ecab716",
    "customerNumber": "CUS-3B9FA4721C8D",
    "customerType": "INDIVIDUAL",
    "customerStatus": "PROSPECT",
    "firstName": "Aarav",
    "middleName": "R.",
    "lastName": "Sharma-Patel",
    "dateOfBirth": "1994-08-15",
    "nationality": "IND",
    "createdAt": "2026-08-28T14:30:00Z",
    "updatedAt": "2026-08-28T14:35:00Z"
  }
}
```

#### cURL Command
```bash
curl -X PUT "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716" \
  -u "ops:Ops@123" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Aarav",
    "middleName": "R.",
    "lastName": "Sharma-Patel",
    "dateOfBirth": "1994-08-15",
    "nationality": "IND"
  }'
```

---

## 4. Module 2: Customer Lifecycle Management

### Allowed State Machine Transitions
* `PROSPECT` &rarr; `START_ONBOARDING` &rarr; `ONBOARDING`
* `ONBOARDING` &rarr; `COMPLETE_ONBOARDING` &rarr; `ACTIVE`
* `ACTIVE` &rarr; `DEACTIVATE` &rarr; `INACTIVE`
* `INACTIVE` &rarr; `REACTIVATE` &rarr; `ACTIVE`
* `ACTIVE` / `INACTIVE` &rarr; `CLOSE` &rarr; `CLOSED`

---

### 2.1 Apply Lifecycle Action
* **HTTP Method**: `POST`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/lifecycle/actions`
* **Access Roles**: `ADMIN`, `OPERATIONS`

#### Valid Dummy Request Body (Step 1: Start Onboarding)
```json
{
  "action": "START_ONBOARDING"
}
```

#### Valid Dummy Request Body (Step 2: Complete Onboarding)
```json
{
  "action": "COMPLETE_ONBOARDING"
}
```

#### Valid Dummy Request Body (Step 3: Deactivate)
```json
{
  "action": "DEACTIVATE"
}
```

#### Valid Dummy Request Body (Step 4: Reactivate)
```json
{
  "action": "REACTIVATE"
}
```

#### Valid Dummy Request Body (Step 5: Close Account)
```json
{
  "action": "CLOSE"
}
```

#### Expected Response (`200 OK`)
```json
{
  "timestamp": "2026-08-28T14:40:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "data": {
    "id": 1,
    "customerId": "e9a073f4-41d6-444f-8f81-2a543ecab716",
    "previousStatus": "PROSPECT",
    "currentStatus": "ONBOARDING",
    "reason": "ONBOARDING_STARTED",
    "effectiveAt": "2026-08-28T14:40:00",
    "createdAt": "2026-08-28T14:40:00",
    "updatedAt": "2026-08-28T14:40:00"
  }
}
```

#### cURL Command
```bash
curl -X POST "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/lifecycle/actions" \
  -u "admin:Admin@123" \
  -H "Content-Type: application/json" \
  -d '{
    "action": "START_ONBOARDING"
  }'
```

#### Negative Test Scenario: Invalid Transition (`400 Bad Request`)
* Attempting `COMPLETE_ONBOARDING` directly while customer is still in `PROSPECT` status triggers error code `CUS-LFC-002` ("Invalid lifecycle transition").

---

### 2.2 Get Current Lifecycle State
* **HTTP Method**: `GET`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/lifecycle/current`
* **Access Roles**: `ADMIN`, `OPERATIONS`, `AUDITOR`

#### Expected Response (`200 OK`)
```json
{
  "timestamp": "2026-08-28T14:42:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "data": {
    "id": 1,
    "customerId": "e9a073f4-41d6-444f-8f81-2a543ecab716",
    "previousStatus": "PROSPECT",
    "currentStatus": "ONBOARDING",
    "reason": "ONBOARDING_STARTED",
    "effectiveAt": "2026-08-28T14:40:00",
    "createdAt": "2026-08-28T14:40:00",
    "updatedAt": "2026-08-28T14:40:00"
  }
}
```

#### cURL Command
```bash
curl -X GET "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/lifecycle/current" \
  -u "ops:Ops@123"
```

---

### 2.3 Get Lifecycle History
* **HTTP Method**: `GET`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/lifecycle`
* **Access Roles**: `ADMIN`, `OPERATIONS`, `AUDITOR`

#### Expected Response (`200 OK`)
```json
{
  "timestamp": "2026-08-28T14:45:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "data": [
    {
      "id": 1,
      "customerId": "e9a073f4-41d6-444f-8f81-2a543ecab716",
      "previousStatus": "PROSPECT",
      "currentStatus": "ONBOARDING",
      "reason": "ONBOARDING_STARTED",
      "effectiveAt": "2026-08-28T14:40:00",
      "createdAt": "2026-08-28T14:40:00",
      "updatedAt": "2026-08-28T14:40:00"
    }
  ]
}
```

#### cURL Command
```bash
curl -X GET "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/lifecycle" \
  -u "auditor:Auditor@123"
```

---

## 5. Module 3: Customer Contact Management

### 3.1 Add Contact (Email / Mobile)
* **HTTP Method**: `POST`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/contacts`
* **Access Roles**: `ADMIN`, `OPERATIONS`

#### Valid Dummy Request Body (Email Contact)
```json
{
  "contactType": "EMAIL",
  "contactValue": "aarav.sharma@example.com",
  "primary": true
}
```

#### Valid Dummy Request Body (Mobile Contact)
```json
{
  "contactType": "MOBILE",
  "contactValue": "+919876543210",
  "primary": false
}
```

#### Expected Response (`201 Created`)
```json
{
  "timestamp": "2026-08-28T14:48:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "data": {
    "id": 1,
    "customerId": "e9a073f4-41d6-444f-8f81-2a543ecab716",
    "contactType": "EMAIL",
    "contactValue": "aarav.sharma@example.com",
    "primary": true,
    "verificationStatus": "UNVERIFIED",
    "status": "ACTIVE",
    "verifiedAt": null,
    "createdAt": "2026-08-28T14:48:00",
    "updatedAt": "2026-08-28T14:48:00"
  }
}
```

#### cURL Command
```bash
curl -X POST "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/contacts" \
  -u "admin:Admin@123" \
  -H "Content-Type: application/json" \
  -d '{
    "contactType": "EMAIL",
    "contactValue": "aarav.sharma@example.com",
    "primary": true
  }'
```

---

### 3.2 List All Customer Contacts
* **HTTP Method**: `GET`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/contacts`
* **Access Roles**: `ADMIN`, `OPERATIONS`, `AUDITOR`

#### Expected Response (`200 OK`)
```json
{
  "timestamp": "2026-08-28T14:50:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "data": [
    {
      "id": 1,
      "customerId": "e9a073f4-41d6-444f-8f81-2a543ecab716",
      "contactType": "EMAIL",
      "contactValue": "aarav.sharma@example.com",
      "primary": true,
      "verificationStatus": "UNVERIFIED",
      "status": "ACTIVE",
      "verifiedAt": null,
      "createdAt": "2026-08-28T14:48:00",
      "updatedAt": "2026-08-28T14:48:00"
    }
  ]
}
```

#### cURL Command
```bash
curl -X GET "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/contacts" \
  -u "ops:Ops@123"
```

---

### 3.3 Set Contact as Primary
* **HTTP Method**: `PATCH`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/contacts/1/primary`
* **Access Roles**: `ADMIN`, `OPERATIONS`

#### cURL Command
```bash
curl -X PATCH "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/contacts/1/primary" \
  -u "admin:Admin@123"
```

---

### 3.4 Verify Contact
* **HTTP Method**: `PATCH`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/contacts/1/verify`
* **Access Roles**: `ADMIN`, `OPERATIONS`

#### Expected Response (`200 OK`)
```json
{
  "timestamp": "2026-08-28T14:52:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "data": {
    "id": 1,
    "customerId": "e9a073f4-41d6-444f-8f81-2a543ecab716",
    "contactType": "EMAIL",
    "contactValue": "aarav.sharma@example.com",
    "primary": true,
    "verificationStatus": "VERIFIED",
    "status": "ACTIVE",
    "verifiedAt": "2026-08-28T14:52:00",
    "createdAt": "2026-08-28T14:48:00",
    "updatedAt": "2026-08-28T14:52:00"
  }
}
```

#### cURL Command
```bash
curl -X PATCH "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/contacts/1/verify" \
  -u "ops:Ops@123"
```

---

### 3.5 Deactivate Contact
* **HTTP Method**: `PATCH`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/contacts/1/deactivate`
* **Access Roles**: `ADMIN`, `OPERATIONS`

#### cURL Command
```bash
curl -X PATCH "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/contacts/1/deactivate" \
  -u "admin:Admin@123"
```

---

## 6. Module 4: Customer Address Management

### 4.1 Add Address
* **HTTP Method**: `POST`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/addresses`
* **Access Roles**: `ADMIN`, `OPERATIONS`
* **Address Types**: `PERMANENT`, `CURRENT`, `WORK`

#### Valid Dummy Request Body (Permanent Address)
```json
{
  "addressType": "PERMANENT",
  "addressLine1": "Flat 402, High-Tech Residency",
  "addressLine2": "Road No 12, Banjara Hills",
  "landmark": "Opposite City Centre Mall",
  "city": "Hyderabad",
  "district": "Hyderabad",
  "state": "Telangana",
  "country": "India",
  "postalCode": "500034"
}
```

#### Valid Dummy Request Body (Work Address)
```json
{
  "addressType": "WORK",
  "addressLine1": "Cyber Towers, 6th Floor",
  "addressLine2": "HITEC City, Madhapur",
  "landmark": "Near HITEC Metro Station",
  "city": "Hyderabad",
  "district": "Rangareddy",
  "state": "Telangana",
  "country": "India",
  "postalCode": "500081"
}
```

#### Expected Response (`201 Created`)
```json
{
  "timestamp": "2026-08-28T14:55:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "data": {
    "id": 1,
    "customerId": "e9a073f4-41d6-444f-8f81-2a543ecab716",
    "addressType": "PERMANENT",
    "addressLine1": "Flat 402, High-Tech Residency",
    "addressLine2": "Road No 12, Banjara Hills",
    "landmark": "Opposite City Centre Mall",
    "city": "Hyderabad",
    "district": "Hyderabad",
    "state": "Telangana",
    "country": "India",
    "postalCode": "500034",
    "status": "ACTIVE",
    "createdAt": "2026-08-28T14:55:00",
    "updatedAt": "2026-08-28T14:55:00"
  }
}
```

#### cURL Command
```bash
curl -X POST "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/addresses" \
  -u "admin:Admin@123" \
  -H "Content-Type: application/json" \
  -d '{
    "addressType": "PERMANENT",
    "addressLine1": "Flat 402, High-Tech Residency",
    "addressLine2": "Road No 12, Banjara Hills",
    "landmark": "Opposite City Centre Mall",
    "city": "Hyderabad",
    "district": "Hyderabad",
    "state": "Telangana",
    "country": "India",
    "postalCode": "500034"
  }'
```

---

### 4.2 List Customer Addresses
* **HTTP Method**: `GET`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/addresses`
* **Access Roles**: `ADMIN`, `OPERATIONS`, `AUDITOR`

#### cURL Command
```bash
curl -X GET "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/addresses" \
  -u "auditor:Auditor@123"
```

---

### 4.3 Get Address By ID
* **HTTP Method**: `GET`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/addresses/1`
* **Access Roles**: `ADMIN`, `OPERATIONS`, `AUDITOR`

#### cURL Command
```bash
curl -X GET "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/addresses/1" \
  -u "ops:Ops@123"
```

---

### 4.4 Update Customer Address
* **HTTP Method**: `PUT`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/addresses/1`
* **Access Roles**: `ADMIN`, `OPERATIONS`

#### Valid Dummy Request Body
```json
{
  "addressType": "PERMANENT",
  "addressLine1": "Flat 505, High-Tech Residency",
  "addressLine2": "Road No 12, Banjara Hills",
  "landmark": "Opposite City Centre Mall",
  "city": "Hyderabad",
  "district": "Hyderabad",
  "state": "Telangana",
  "country": "India",
  "postalCode": "500034"
}
```

#### cURL Command
```bash
curl -X PUT "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/addresses/1" \
  -u "admin:Admin@123" \
  -H "Content-Type: application/json" \
  -d '{
    "addressType": "PERMANENT",
    "addressLine1": "Flat 505, High-Tech Residency",
    "addressLine2": "Road No 12, Banjara Hills",
    "landmark": "Opposite City Centre Mall",
    "city": "Hyderabad",
    "district": "Hyderabad",
    "state": "Telangana",
    "country": "India",
    "postalCode": "500034"
  }'
```

---

### 4.5 Deactivate Address
* **HTTP Method**: `PATCH`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/addresses/1/deactivate`
* **Access Roles**: `ADMIN`, `OPERATIONS`

#### cURL Command
```bash
curl -X PATCH "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/addresses/1/deactivate" \
  -u "admin:Admin@123"
```

---

## 7. Module 5: Customer Preferences Management

### 5.1 Create Customer Preferences
* **HTTP Method**: `POST`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/preferences`
* **Access Roles**: `ADMIN`, `OPERATIONS`
* **Languages Supported**: `ENGLISH`, `HINDI`, `MARATHI`
* **Channels Supported**: `EMAIL`, `SMS`, `PUSH`

#### Valid Dummy Request Body
```json
{
  "preferredLanguage": "ENGLISH",
  "preferredCommunicationChannel": "SMS",
  "marketingNotifications": true,
  "transactionNotifications": true,
  "securityNotifications": true
}
```

#### Expected Response (`201 Created`)
```json
{
  "timestamp": "2026-08-28T15:00:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "data": {
    "id": 1,
    "customerId": "e9a073f4-41d6-444f-8f81-2a543ecab716",
    "preferredLanguage": "ENGLISH",
    "preferredCommunicationChannel": "SMS",
    "marketingNotifications": true,
    "transactionNotifications": true,
    "securityNotifications": true,
    "status": "ACTIVE",
    "createdAt": "2026-08-28T15:00:00",
    "updatedAt": "2026-08-28T15:00:00"
  }
}
```

#### cURL Command
```bash
curl -X POST "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/preferences" \
  -u "admin:Admin@123" \
  -H "Content-Type: application/json" \
  -d '{
    "preferredLanguage": "ENGLISH",
    "preferredCommunicationChannel": "SMS",
    "marketingNotifications": true,
    "transactionNotifications": true,
    "securityNotifications": true
  }'
```

---

### 5.2 Get Customer Preferences
* **HTTP Method**: `GET`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/preferences`
* **Access Roles**: `ADMIN`, `OPERATIONS`, `AUDITOR`

#### cURL Command
```bash
curl -X GET "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/preferences" \
  -u "auditor:Auditor@123"
```

---

### 5.3 Update Customer Preferences (Partial Update)
* **HTTP Method**: `PATCH`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/preferences`
* **Access Roles**: `ADMIN`, `OPERATIONS`

#### Valid Dummy Request Body
```json
{
  "preferredLanguage": "HINDI",
  "preferredCommunicationChannel": "PUSH",
  "marketingNotifications": false
}
```

#### cURL Command
```bash
curl -X PATCH "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/preferences" \
  -u "ops:Ops@123" \
  -H "Content-Type: application/json" \
  -d '{
    "preferredLanguage": "HINDI",
    "preferredCommunicationChannel": "PUSH",
    "marketingNotifications": false
  }'
```

---

### 5.4 Deactivate Customer Preferences
* **HTTP Method**: `PATCH`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/preferences/deactivate`
* **Access Roles**: `ADMIN`, `OPERATIONS`

#### cURL Command
```bash
curl -X PATCH "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/preferences/deactivate" \
  -u "admin:Admin@123"
```

---

## 8. Module 6: Customer Consent Management

### Supported Consent Types & Sources
* **Consent Types**:
  * `TERMS_AND_CONDITIONS`
  * `PRIVACY_POLICY`
  * `MARKETING_COMMUNICATION`
  * `DATA_PROCESSING`
  * `THIRD_PARTY_DATA_SHARING`
  * `PERSONALIZED_OFFERS`
* **Consent Sources**: `WEB`, `MOBILE`, `ADMIN`, `API`

---

### 6.1 Grant Consent
* **HTTP Method**: `POST`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/consents`
* **Access Roles**: `ADMIN`, `OPERATIONS`

#### Valid Dummy Request Body (Terms & Conditions)
```json
{
  "consentType": "TERMS_AND_CONDITIONS",
  "consentVersion": "v2.1",
  "consentTextVersion": "v2.1-terms-2026",
  "source": "MOBILE"
}
```

#### Valid Dummy Request Body (Data Processing)
```json
{
  "consentType": "DATA_PROCESSING",
  "consentVersion": "v1.0",
  "consentTextVersion": "v1.0-data-proc",
  "source": "WEB"
}
```

#### Expected Response (`201 Created`)
```json
{
  "timestamp": "2026-08-28T15:05:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "data": {
    "id": 1,
    "customerId": "e9a073f4-41d6-444f-8f81-2a543ecab716",
    "consentType": "TERMS_AND_CONDITIONS",
    "status": "GRANTED",
    "consentVersion": "v2.1",
    "consentTextVersion": "v2.1-terms-2026",
    "source": "MOBILE",
    "grantedAt": "2026-08-28T15:05:00",
    "withdrawnAt": null,
    "createdAt": "2026-08-28T15:05:00"
  }
}
```

#### cURL Command
```bash
curl -X POST "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/consents" \
  -u "admin:Admin@123" \
  -H "Content-Type: application/json" \
  -d '{
    "consentType": "TERMS_AND_CONDITIONS",
    "consentVersion": "v2.1",
    "consentTextVersion": "v2.1-terms-2026",
    "source": "MOBILE"
  }'
```

---

### 6.2 Withdraw Consent
* **HTTP Method**: `PATCH`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/consents/withdraw`
* **Access Roles**: `ADMIN`, `OPERATIONS`

#### Valid Dummy Request Body
```json
{
  "consentType": "TERMS_AND_CONDITIONS",
  "source": "MOBILE"
}
```

#### Expected Response (`200 OK`)
```json
{
  "timestamp": "2026-08-28T15:08:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "data": {
    "id": 1,
    "customerId": "e9a073f4-41d6-444f-8f81-2a543ecab716",
    "consentType": "TERMS_AND_CONDITIONS",
    "status": "WITHDRAWN",
    "consentVersion": "v2.1",
    "consentTextVersion": "v2.1-terms-2026",
    "source": "MOBILE",
    "grantedAt": "2026-08-28T15:05:00",
    "withdrawnAt": "2026-08-28T15:08:00",
    "createdAt": "2026-08-28T15:05:00"
  }
}
```

#### cURL Command
```bash
curl -X PATCH "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/consents/withdraw" \
  -u "ops:Ops@123" \
  -H "Content-Type: application/json" \
  -d '{
    "consentType": "TERMS_AND_CONDITIONS",
    "source": "MOBILE"
  }'
```

---

### 6.3 Get Full Consent History
* **HTTP Method**: `GET`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/consents`
* **Access Roles**: `ADMIN`, `OPERATIONS`, `AUDITOR`

#### cURL Command
```bash
curl -X GET "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/consents" \
  -u "auditor:Auditor@123"
```

---

### 6.4 Get Consent History By Type
* **HTTP Method**: `GET`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/consents/TERMS_AND_CONDITIONS`
* **Access Roles**: `ADMIN`, `OPERATIONS`, `AUDITOR`

#### cURL Command
```bash
curl -X GET "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/consents/TERMS_AND_CONDITIONS" \
  -u "ops:Ops@123"
```

---

### 6.5 Get Latest Consent By Type
* **HTTP Method**: `GET`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/consents/TERMS_AND_CONDITIONS/latest`
* **Access Roles**: `ADMIN`, `OPERATIONS`, `AUDITOR`

#### cURL Command
```bash
curl -X GET "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/consents/TERMS_AND_CONDITIONS/latest" \
  -u "admin:Admin@123"
```

---

## 9. Module 7: Customer Audit & Trail Management

### 7.1 Query Customer Audit Trail (Default: Last 30 Days)
* **HTTP Method**: `GET`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/audit`
* **Access Roles**: `ADMIN`, `OPERATIONS`, `AUDITOR`

#### Expected Response (`200 OK`)
```json
{
  "timestamp": "2026-08-28T15:15:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "data": [
    {
      "customerId": "e9a073f4-41d6-444f-8f81-2a543ecab716",
      "action": "CUSTOMER_CREATED",
      "previousStatus": null,
      "newStatus": "PROSPECT",
      "reason": "Customer profile created",
      "actorId": "admin",
      "actorType": "USER",
      "correlationId": "550e8400-e29b-41d4-a716-446655440000",
      "occurredAt": "2026-08-28T14:30:00",
      "source": "CUSTOMER_SERVICE"
    },
    {
      "customerId": "e9a073f4-41d6-444f-8f81-2a543ecab716",
      "action": "LIFECYCLE_CHANGED",
      "previousStatus": "PROSPECT",
      "newStatus": "ONBOARDING",
      "reason": "ONBOARDING_STARTED",
      "actorId": "admin",
      "actorType": "USER",
      "correlationId": "550e8400-e29b-41d4-a716-446655440000",
      "occurredAt": "2026-08-28T14:40:00",
      "source": "CUSTOMER_SERVICE"
    }
  ]
}
```

#### cURL Command
```bash
curl -X GET "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/audit" \
  -u "auditor:Auditor@123"
```

---

### 7.2 Query Audit Trail with Date Filters
* **HTTP Method**: `GET`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/audit?from=2026-08-01T00:00:00&to=2026-08-30T23:59:59`
* **Access Roles**: `ADMIN`, `OPERATIONS`, `AUDITOR`

#### cURL Command
```bash
curl -X GET "http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716/audit?from=2026-08-01T00:00:00&to=2026-08-30T23:59:59" \
  -u "auditor:Auditor@123"
```

---

## 10. Module 8: Health & OpenAPI Documentation (Public)

No authentication required for these endpoints.

| Name | Type | Full URL | Expected Status |
| :--- | :--- | :--- | :---: |
| **Actuator Health Check** | `GET` | `http://localhost:8081/actuator/health` | `200 OK` (`{"status":"UP"}`) |
| **Actuator Info** | `GET` | `http://localhost:8081/actuator/info` | `200 OK` |
| **Actuator Metrics** | `GET` | `http://localhost:8081/actuator/metrics` | `200 OK` |
| **OpenAPI 3 JSON Docs** | `GET` | `http://localhost:8081/v3/api-docs` | `200 OK` |
| **Swagger UI Page** | `GET` | `http://localhost:8081/swagger-ui.html` | `200 OK` (HTML) |

#### cURL Command (Health Check)
```bash
curl -X GET "http://localhost:8081/actuator/health"
```

---

## 11. Security Negative Tests (401 & 403)

### 11.1 Unauthorized Request (Missing / Invalid Auth)
* **HTTP Method**: `GET`
* **Full URL**: `http://localhost:8081/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716`
* **Headers**: *No Authorization Header*
* **Expected Status**: `401 Unauthorized`
* **Expected Error Response**:
```json
{
  "timestamp": "2026-08-28T15:20:00Z",
  "status": 401,
  "errorCode": "CUS-SEC-401",
  "message": "Authentication required",
  "path": "/api/v1/customers/e9a073f4-41d6-444f-8f81-2a543ecab716",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### 11.2 Forbidden Request (Insufficient Role)
* **HTTP Method**: `POST`
* **Full URL**: `http://localhost:8081/api/v1/customers`
* **Credentials**: `auditor:Auditor@123` (Auditor role cannot create customer)
* **Expected Status**: `403 Forbidden`
* **Expected Error Response**:
```json
{
  "timestamp": "2026-08-28T15:20:00Z",
  "status": 403,
  "errorCode": "CUS-SEC-403",
  "message": "Access denied",
  "path": "/api/v1/customers",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

## 12. Complete End-to-End API Test Script (PowerShell & Bash)

### Shell Script (`test-all-apis.sh`)

```bash
#!/bin/bash
set -e

BASE_URL="http://localhost:8081/api/v1"
AUTH_ADMIN="admin:Admin@123"
AUTH_AUDITOR="auditor:Auditor@123"

echo "=== Step 1: Health Check ==="
curl -s -f "http://localhost:8081/actuator/health" | grep "UP"
echo " -> Server is UP!"

echo "=== Step 2: Create Customer ==="
CREATE_RES=$(curl -s -X POST "$BASE_URL/customers" \
  -u "$AUTH_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerType": "INDIVIDUAL",
    "firstName": "Siddharth",
    "middleName": "Dev",
    "lastName": "Verma",
    "dateOfBirth": "1992-11-20",
    "nationality": "IND"
  }')
echo "$CREATE_RES"

CUSTOMER_ID=$(echo "$CREATE_RES" | grep -o '"id":"[^"]*' | cut -d'"' -f4)
echo "Generated Customer ID: $CUSTOMER_ID"

echo "=== Step 3: Add Customer Email Contact ==="
curl -s -X POST "$BASE_URL/customers/$CUSTOMER_ID/contacts" \
  -u "$AUTH_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "contactType": "EMAIL",
    "contactValue": "siddharth.verma@example.com",
    "primary": true
  }'

echo "=== Step 4: Add Customer Address ==="
curl -s -X POST "$BASE_URL/customers/$CUSTOMER_ID/addresses" \
  -u "$AUTH_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "addressType": "PERMANENT",
    "addressLine1": "Tower 5, Flat 1204",
    "addressLine2": "Greenwood Estates",
    "landmark": "Near Metro",
    "city": "Mumbai",
    "district": "Mumbai Suburban",
    "state": "Maharashtra",
    "country": "India",
    "postalCode": "400050"
  }'

echo "=== Step 5: Create Customer Preferences ==="
curl -s -X POST "$BASE_URL/customers/$CUSTOMER_ID/preferences" \
  -u "$AUTH_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "preferredLanguage": "ENGLISH",
    "preferredCommunicationChannel": "EMAIL",
    "marketingNotifications": false,
    "transactionNotifications": true,
    "securityNotifications": true
  }'

echo "=== Step 6: Grant Consent ==="
curl -s -X POST "$BASE_URL/customers/$CUSTOMER_ID/consents" \
  -u "$AUTH_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "consentType": "TERMS_AND_CONDITIONS",
    "consentVersion": "v1.0",
    "consentTextVersion": "v1.0-legal",
    "source": "WEB"
  }'

echo "=== Step 7: Transition Lifecycle: START_ONBOARDING ==="
curl -s -X POST "$BASE_URL/customers/$CUSTOMER_ID/lifecycle/actions" \
  -u "$AUTH_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{"action": "START_ONBOARDING"}'

echo "=== Step 8: Transition Lifecycle: COMPLETE_ONBOARDING ==="
curl -s -X POST "$BASE_URL/customers/$CUSTOMER_ID/lifecycle/actions" \
  -u "$AUTH_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{"action": "COMPLETE_ONBOARDING"}'

echo "=== Step 9: Query Audit History ==="
curl -s -X GET "$BASE_URL/customers/$CUSTOMER_ID/audit" \
  -u "$AUTH_AUDITOR"

echo ""
echo "=== All Steps Completed Successfully! ==="
```

---

## 13. Summary Matrix of All Endpoints

| # | Module | Method | Full URL | Required Role | Request Body |
| :---: | :--- | :---: | :--- | :--- | :---: |
| 1 | **Customer** | `POST` | `http://localhost:8081/api/v1/customers` | `OPERATIONS`, `ADMIN` | `CreateCustomerRequest` |
| 2 | **Customer** | `GET` | `http://localhost:8081/api/v1/customers/{customerId}` | `OPERATIONS`, `ADMIN`, `AUDITOR` | None |
| 3 | **Customer** | `PUT` | `http://localhost:8081/api/v1/customers/{customerId}` | `OPERATIONS`, `ADMIN` | `UpdateCustomerRequest` |
| 4 | **Lifecycle** | `POST` | `http://localhost:8081/api/v1/customers/{customerId}/lifecycle/actions` | `OPERATIONS`, `ADMIN` | `CustomerLifecycleActionRequest` |
| 5 | **Lifecycle** | `GET` | `http://localhost:8081/api/v1/customers/{customerId}/lifecycle/current` | `OPERATIONS`, `ADMIN`, `AUDITOR` | None |
| 6 | **Lifecycle** | `GET` | `http://localhost:8081/api/v1/customers/{customerId}/lifecycle` | `OPERATIONS`, `ADMIN`, `AUDITOR` | None |
| 7 | **Contact** | `POST` | `http://localhost:8081/api/v1/customers/{customerId}/contacts` | `OPERATIONS`, `ADMIN` | `AddCustomerContactRequest` |
| 8 | **Contact** | `GET` | `http://localhost:8081/api/v1/customers/{customerId}/contacts` | `OPERATIONS`, `ADMIN`, `AUDITOR` | None |
| 9 | **Contact** | `PATCH` | `http://localhost:8081/api/v1/customers/{customerId}/contacts/{contactId}/primary` | `OPERATIONS`, `ADMIN` | None |
| 10 | **Contact** | `PATCH` | `http://localhost:8081/api/v1/customers/{customerId}/contacts/{contactId}/verify` | `OPERATIONS`, `ADMIN` | None |
| 11 | **Contact** | `PATCH` | `http://localhost:8081/api/v1/customers/{customerId}/contacts/{contactId}/deactivate` | `OPERATIONS`, `ADMIN` | None |
| 12 | **Address** | `POST` | `http://localhost:8081/api/v1/customers/{customerId}/addresses` | `OPERATIONS`, `ADMIN` | `AddCustomerAddressRequest` |
| 13 | **Address** | `GET` | `http://localhost:8081/api/v1/customers/{customerId}/addresses` | `OPERATIONS`, `ADMIN`, `AUDITOR` | None |
| 14 | **Address** | `GET` | `http://localhost:8081/api/v1/customers/{customerId}/addresses/{addressId}` | `OPERATIONS`, `ADMIN`, `AUDITOR` | None |
| 15 | **Address** | `PUT` | `http://localhost:8081/api/v1/customers/{customerId}/addresses/{addressId}` | `OPERATIONS`, `ADMIN` | `AddCustomerAddressRequest` |
| 16 | **Address** | `PATCH` | `http://localhost:8081/api/v1/customers/{customerId}/addresses/{addressId}/deactivate` | `OPERATIONS`, `ADMIN` | None |
| 17 | **Preferences** | `POST` | `http://localhost:8081/api/v1/customers/{customerId}/preferences` | `OPERATIONS`, `ADMIN` | `CreateCustomerPreferenceRequest` |
| 18 | **Preferences** | `GET` | `http://localhost:8081/api/v1/customers/{customerId}/preferences` | `OPERATIONS`, `ADMIN`, `AUDITOR` | None |
| 19 | **Preferences** | `PATCH` | `http://localhost:8081/api/v1/customers/{customerId}/preferences` | `OPERATIONS`, `ADMIN` | `UpdateCustomerPreferenceRequest` |
| 20 | **Preferences** | `PATCH` | `http://localhost:8081/api/v1/customers/{customerId}/preferences/deactivate` | `OPERATIONS`, `ADMIN` | None |
| 21 | **Consent** | `POST` | `http://localhost:8081/api/v1/customers/{customerId}/consents` | `OPERATIONS`, `ADMIN` | `GrantCustomerConsentRequest` |
| 22 | **Consent** | `PATCH` | `http://localhost:8081/api/v1/customers/{customerId}/consents/withdraw` | `OPERATIONS`, `ADMIN` | `WithdrawCustomerConsentRequest` |
| 23 | **Consent** | `GET` | `http://localhost:8081/api/v1/customers/{customerId}/consents` | `OPERATIONS`, `ADMIN`, `AUDITOR` | None |
| 24 | **Consent** | `GET` | `http://localhost:8081/api/v1/customers/{customerId}/consents/{consentType}` | `OPERATIONS`, `ADMIN`, `AUDITOR` | None |
| 25 | **Consent** | `GET` | `http://localhost:8081/api/v1/customers/{customerId}/consents/{consentType}/latest` | `OPERATIONS`, `ADMIN`, `AUDITOR` | None |
| 26 | **Audit** | `GET` | `http://localhost:8081/api/v1/customers/{customerId}/audit` | `AUDITOR`, `OPERATIONS`, `ADMIN` | None |
| 27 | **Monitoring** | `GET` | `http://localhost:8081/actuator/health` | Public | None |
| 28 | **Monitoring** | `GET` | `http://localhost:8081/swagger-ui.html` | Public | None |
