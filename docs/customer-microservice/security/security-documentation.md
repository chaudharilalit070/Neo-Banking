# Customer Microservice — Security & Authentication Architecture

## 1. Security Architecture Summary

The Customer Microservice operates on a zero-trust model:
- **Authentication & Authorization**: Handled via Spring Security 6 filter chain and method-level security (`@EnableMethodSecurity`).
- **Stateless Session Management**: `SessionCreationPolicy.STATELESS` ensures no HTTP sessions are retained on the server.
- **CORS & CSRF**: CSRF disabled for stateless REST endpoints; CORS configured for preflight validation.
- **Error Response Standard**: Unauthenticated (401) and Forbidden (403) errors return structured JSON `ErrorResponse` objects with standard error codes and correlation IDs.

---

## 2. Authentication Entry Point & Access Denied Handlers

In `SecurityConfig.java`:
- **401 Unauthorized**: Handled by custom `AuthenticationEntryPoint`. Returns HTTP 401 with `ErrorCodes.UNAUTHORIZED` (`"CUS-SEC-401"`).
- **403 Forbidden**: Handled by custom `AccessDeniedHandler`. Returns HTTP 403 with `ErrorCodes.FORBIDDEN` (`"CUS-SEC-403"`).

---

## 3. Correlation ID & Audit Context

- **`CorrelationIdFilter`**: Inspects incoming HTTP requests for `X-Correlation-Id`. If absent, generates a new UUID and sets it in SLF4J MDC and the HTTP response header `X-Correlation-Id`.
- **`CurrentUserProvider`**: Extracts the current authenticated user's ID from `SecurityContextHolder.getContext().getAuthentication()`. Falls back to `"SYSTEM"` if unauthenticated.
