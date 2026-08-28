package com.neobank.neobank_backend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobank.neobank_backend.common.constants.ErrorCodes;
import com.neobank.neobank_backend.customer.api.request.CreateCustomerRequest;
import com.neobank.neobank_backend.customer.api.request.UpdateCustomerRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CustomerControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/customers - 201 Created with valid payload")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateCustomerSuccess() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                CustomerType.INDIVIDUAL,
                "John",
                "David",
                "Doe",
                LocalDate.of(1990, 5, 15),
                "USA"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.customerNumber").exists())
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.customerStatus").value("PROSPECT"));
    }

    @Test
    @DisplayName("POST /api/v1/customers - 400 Bad Request on missing fields")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateCustomerValidationFailure() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                null,
                "",
                null,
                "",
                null,
                "INVALID_NATIONALITY_TOO_LONG"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.VALIDATION_ERROR));
    }

    @Test
    @DisplayName("GET /api/v1/customers/{customerId} - 200 OK after creation")
    @WithMockUser(username = "ops", roles = {"OPERATIONS"})
    void testGetCustomerByIdSuccess() throws Exception {
        CreateCustomerRequest createRequest = new CreateCustomerRequest(
                CustomerType.INDIVIDUAL,
                "Jane",
                null,
                "Smith",
                LocalDate.of(1992, 8, 22),
                "USA"
        );

        String createResponse = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String customerId = objectMapper.readTree(createResponse).path("data").path("id").asText();

        mockMvc.perform(get("/api/v1/customers/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(customerId))
                .andExpect(jsonPath("$.data.firstName").value("Jane"))
                .andExpect(jsonPath("$.data.lastName").value("Smith"));
    }

    @Test
    @DisplayName("GET /api/v1/customers/{customerId} - 404 Not Found for nonexistent ID")
    @WithMockUser(username = "auditor", roles = {"AUDITOR"})
    void testGetCustomerNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(get("/api/v1/customers/{customerId}", randomId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.CUSTOMER_NOT_FOUND));
    }

    @Test
    @DisplayName("PUT /api/v1/customers/{customerId} - 200 OK on update")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateCustomerSuccess() throws Exception {
        CreateCustomerRequest createRequest = new CreateCustomerRequest(
                CustomerType.INDIVIDUAL,
                "Robert",
                null,
                "Martin",
                LocalDate.of(1980, 1, 1),
                "USA"
        );

        String createResponse = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String customerId = objectMapper.readTree(createResponse).path("data").path("id").asText();

        UpdateCustomerRequest updateRequest = new UpdateCustomerRequest(
                "Uncle",
                "Bob",
                "Martin",
                LocalDate.of(1980, 1, 1),
                "USA"
        );

        mockMvc.perform(put("/api/v1/customers/{customerId}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Uncle"))
                .andExpect(jsonPath("$.data.middleName").value("Bob"));
    }
}
