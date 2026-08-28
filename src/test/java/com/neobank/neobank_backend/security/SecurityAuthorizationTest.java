package com.neobank.neobank_backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobank.neobank_backend.common.constants.ErrorCodes;
import com.neobank.neobank_backend.customer.api.request.CreateCustomerRequest;
import com.neobank.neobank_backend.customer.domain.CustomerType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Unauthenticated request to protected endpoint returns 401 Unauthorized with JSON ErrorResponse")
    void testUnauthenticatedAccessReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/customers/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.UNAUTHORIZED))
                .andExpect(jsonPath("$.message").value("Authentication required"));
    }

    @Test
    @DisplayName("Forbidden role returns 403 Forbidden with JSON ErrorResponse")
    @WithMockUser(username = "guest_user", roles = {"CUSTOMER"})
    void testForbiddenRoleReturns403() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                CustomerType.INDIVIDUAL,
                "Unauthorized",
                null,
                "User",
                LocalDate.of(1990, 1, 1),
                "USA"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.FORBIDDEN))
                .andExpect(jsonPath("$.message").value("Access denied"));
    }

    @Test
    @DisplayName("Actuator health endpoint is publicly accessible without authentication")
    void testActuatorHealthPermitAll() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Auditor role has read access to audit endpoints")
    @WithMockUser(username = "auditor_user", roles = {"AUDITOR"})
    void testAuditorAccessToAuditHistory() throws Exception {
        mockMvc.perform(get("/api/v1/customers/" + UUID.randomUUID() + "/audit"))
                .andExpect(status().isOk());
    }
}
