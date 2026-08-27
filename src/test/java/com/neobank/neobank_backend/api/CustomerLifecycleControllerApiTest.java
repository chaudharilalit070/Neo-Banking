package com.neobank.neobank_backend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobank.neobank_backend.customer.api.request.CreateCustomerRequest;
import com.neobank.neobank_backend.customer.domain.CustomerType;
import com.neobank.neobank_backend.lifecycle.api.request.CustomerLifecycleActionRequest;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycleAction;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CustomerLifecycleControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String createTestCustomer() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                CustomerType.INDIVIDUAL,
                "Lifecycle",
                "Tester",
                "User",
                LocalDate.of(1993, 3, 15),
                "USA"
        );

        String response = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).path("data").path("id").asText();
    }

    @Test
    @DisplayName("POST /api/v1/customers/{id}/lifecycle/actions - START_ONBOARDING transitions PROSPECT to ONBOARDING")
    @WithMockUser(username = "ops", roles = {"OPERATIONS"})
    void testApplyLifecycleAction() throws Exception {
        String customerId = createTestCustomer();

        CustomerLifecycleActionRequest actionRequest = new CustomerLifecycleActionRequest(
                CustomerLifecycleAction.START_ONBOARDING
        );

        mockMvc.perform(post("/api/v1/customers/{customerId}/lifecycle/actions", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.previousStatus").value("PROSPECT"))
                .andExpect(jsonPath("$.data.currentStatus").value("ONBOARDING"))
                .andExpect(jsonPath("$.data.reason").value("ONBOARDING_STARTED"));
    }

    @Test
    @DisplayName("POST /api/v1/customers/{id}/lifecycle/actions - Invalid transition returns 400 Bad Request")
    @WithMockUser(username = "ops", roles = {"OPERATIONS"})
    void testInvalidLifecycleTransition() throws Exception {
        String customerId = createTestCustomer();

        // DEACTIVATE is invalid from PROSPECT
        CustomerLifecycleActionRequest invalidAction = new CustomerLifecycleActionRequest(
                CustomerLifecycleAction.DEACTIVATE
        );

        mockMvc.perform(post("/api/v1/customers/{customerId}/lifecycle/actions", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAction)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_LIFECYCLE_TRANSITION"));
    }

    @Test
    @DisplayName("GET /api/v1/customers/{id}/lifecycle/current - 200 OK returns current state")
    @WithMockUser(username = "auditor", roles = {"AUDITOR"})
    void testGetCurrentLifecycle() throws Exception {
        String customerId = createTestCustomer();

        mockMvc.perform(get("/api/v1/customers/{customerId}/lifecycle/current", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentStatus").value("PROSPECT"));
    }

    @Test
    @DisplayName("GET /api/v1/customers/{id}/lifecycle - 200 OK returns lifecycle history")
    @WithMockUser(username = "auditor", roles = {"AUDITOR"})
    void testGetLifecycleHistory() throws Exception {
        String customerId = createTestCustomer();

        // Advance to ONBOARDING
        mockMvc.perform(post("/api/v1/customers/{customerId}/lifecycle/actions", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CustomerLifecycleActionRequest(CustomerLifecycleAction.START_ONBOARDING))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/customers/{customerId}/lifecycle", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/v1/customers/{id}/lifecycle/current - 404 for non-existent customer")
    @WithMockUser(username = "auditor", roles = {"AUDITOR"})
    void testGetLifecycleCustomerNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/customers/{customerId}/lifecycle/current", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("CUSTOMER_NOT_FOUND"));
    }
}
