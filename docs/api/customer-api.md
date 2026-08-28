# Customer Microservice — API Testing Manual & Reference

For the comprehensive, ready-to-test manual with complete dummy payloads, full URLs, HTTP methods, credentials, and cURL commands, see the root file:
- [`API_TESTING_MANUAL.md`](file:///d:/neobank-backend/API_TESTING_MANUAL.md)

---

## Quick Reference Summary

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
| 27 | **Actuator Health** | `GET` | `http://localhost:8081/actuator/health` | Public | None |
| 28 | **Swagger UI** | `GET` | `http://localhost:8081/swagger-ui.html` | Public | None |
| 29 | **OpenAPI JSON** | `GET` | `http://localhost:8081/v3/api-docs` | Public | None |
