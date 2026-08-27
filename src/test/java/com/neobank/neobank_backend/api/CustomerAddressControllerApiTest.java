package com.neobank.neobank_backend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobank.neobank_backend.address.api.request.AddCustomerAddressRequest;
import com.neobank.neobank_backend.address.domain.AddressType;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CustomerAddressControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String createTestCustomer() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                CustomerType.INDIVIDUAL,
                "Address",
                "Tester",
                "User",
                LocalDate.of(1989, 4, 10),
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
    @DisplayName("POST & GET & PUT & PATCH addresses flow")
    @WithMockUser(username = "ops", roles = {"OPERATIONS"})
    void testAddressEndpointsFlow() throws Exception {
        String customerId = createTestCustomer();

        // 1. Add Address
        AddCustomerAddressRequest request = new AddCustomerAddressRequest();
        request.setAddressType(AddressType.PERMANENT);
        request.setAddressLine1("742 Evergreen Terrace");
        request.setCity("Springfield");
        request.setState("OR");
        request.setCountry("USA");
        request.setPostalCode("97477");

        String addResponse = mockMvc.perform(post("/api/v1/customers/{customerId}/addresses", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.addressLine1").value("742 Evergreen Terrace"))
                .andExpect(jsonPath("$.data.city").value("Springfield"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andReturn().getResponse().getContentAsString();

        Long addressId = objectMapper.readTree(addResponse).path("data").path("id").asLong();

        // 2. Get All Addresses
        mockMvc.perform(get("/api/v1/customers/{customerId}/addresses", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(addressId));

        // 3. Get Address By ID
        mockMvc.perform(get("/api/v1/customers/{customerId}/addresses/{addressId}", customerId, addressId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.city").value("Springfield"));

        // 4. Update Address
        request.setAddressLine2("Apt 4B");
        mockMvc.perform(put("/api/v1/customers/{customerId}/addresses/{addressId}", customerId, addressId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.addressLine2").value("Apt 4B"));

        // 5. Deactivate Address
        mockMvc.perform(patch("/api/v1/customers/{customerId}/addresses/{addressId}/deactivate", customerId, addressId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("INACTIVE"));
    }
}
