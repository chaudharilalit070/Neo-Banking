# Security, Authentication & Role-Based Access Control

## Security Architecture Overview

The Customer Microservice enforces a stateless, defense-in-depth security model using Spring Security 6 with Method Security (`@EnableMethodSecurity`).

```
                    Incoming HTTP Request
                              │
                              ▼
                ┌───────────────────────────┐
                │   CorrelationIdFilter     │ ──> Injects / Propagates MDC correlationId
                └─────────────┬─────────────┘
                              │
                              ▼
                ┌───────────────────────────┐
                │   SecurityFilterChain     │
                └─────────────┬─────────────┘
                              │
               ┌──────────────┴──────────────┐
               ▼                             ▼
       [Public Endpoint]             [Protected Endpoint]
    (/actuator/health, info)         (/api/v1/customers/**)
               │                             │
          Allow (200 OK)                     ▼
                               ┌───────────────────────────┐
                               │  Authentication Context   │
                               └─────────────┬─────────────┘
                                             │
                                ┌────────────┴────────────┐
                                ▼                         ▼
                        [No Authentication]       [Authenticated]
                                │                         │
                        401 Unauthorized                  ▼
                     (JSON ErrorResponse)      ┌─────────────────────┐
                                               │   Role Validation   │
                                               │  (@PreAuthorize)    │
                                               └──────────┬──────────┘
                                                          │
                                             ┌────────────┴────────────┐
                                             ▼                         ▼
                                      [Missing Role]             [Role Valid]
                                             │                         │
                                      403 Forbidden              Allow Execution
                                   (JSON ErrorResponse)
```

---

## Role-Based Access Control (RBAC) Matrix

| Endpoint Group | Method & Path | Permitted Roles | Description |
|---|---|---|---|
| **Actuator** | `GET /actuator/health`, `/actuator/info` | `permitAll` | Platform health monitoring |
| **OpenAPI** | `GET /swagger-ui/**`, `/v3/api-docs/**` | `permitAll` | Interactive API documentation |
| **Customer Write** | `POST /api/v1/customers` | `ADMIN`, `OPERATIONS` | Create new customer profile |
| **Customer Update** | `PUT /api/v1/customers/{id}` | `ADMIN`, `OPERATIONS` | Update customer personal details |
| **Customer Read** | `GET /api/v1/customers/{id}` | `ADMIN`, `OPERATIONS`, `AUDITOR` | View customer profile |
| **Lifecycle Actions**| `POST /api/v1/customers/{id}/lifecycle/actions` | `ADMIN`, `OPERATIONS` | Transition customer state |
| **Lifecycle Read** | `GET /api/v1/customers/{id}/lifecycle/**` | `ADMIN`, `OPERATIONS`, `AUDITOR` | View lifecycle history |
| **Contacts Write** | `POST`, `PATCH /api/v1/customers/{id}/contacts/**` | `ADMIN`, `OPERATIONS` | Add/verify/modify contacts |
| **Contacts Read** | `GET /api/v1/customers/{id}/contacts` | `ADMIN`, `OPERATIONS`, `AUDITOR` | View customer contacts |
| **Addresses Write**| `POST`, `PUT`, `PATCH /api/v1/customers/{id}/addresses/**` | `ADMIN`, `OPERATIONS` | Manage physical addresses |
| **Addresses Read** | `GET /api/v1/customers/{id}/addresses/**` | `ADMIN`, `OPERATIONS`, `AUDITOR` | View customer addresses |
| **Preferences** | `POST`, `PATCH, GET /api/v1/customers/{id}/preferences/**` | `ADMIN`, `OPERATIONS`, `AUDITOR` | Manage communication channels |
| **Consents** | `POST`, `PATCH, GET /api/v1/customers/{id}/consents/**` | `ADMIN`, `OPERATIONS`, `AUDITOR` | Manage regulatory consents |
| **Audit Logs** | `GET /api/v1/customers/{id}/audit` | `ADMIN`, `AUDITOR` | Compliance audit inspection |

---

## Standard JSON Error Formats for Security Failures

### 401 Unauthorized (Missing Credentials)
```json
{
  "timestamp": "2026-08-27T16:45:41.497Z",
  "status": 401,
  "errorCode": "CUS-SEC-401",
  "message": "Authentication required",
  "path": "/api/v1/customers/12f1d797-d2bc-405f-96e4-95dd655e91fa",
  "correlationId": "c49a37e5-e01e-450a-8bf7-320959ceae31"
}
```

### 403 Forbidden (Insufficient Privilege)
```json
{
  "timestamp": "2026-08-27T16:45:41.512Z",
  "status": 403,
  "errorCode": "CUS-SEC-403",
  "message": "Access denied",
  "path": "/api/v1/customers",
  "correlationId": "8f39beae-460d-4bd9-974a-47ecb1e5ecfa"
}
```
