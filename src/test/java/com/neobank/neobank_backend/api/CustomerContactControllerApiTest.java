package com.neobank.neobank_backend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobank.neobank_backend.contact.api.request.contact.AddCustomerContactRequest;
import com.neobank.neobank_backend.contact.domain.contact.ContactType;
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
class CustomerContactControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String createTestCustomer() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                CustomerType.INDIVIDUAL,
                "Contact",
                "Tester",
                "User",
                LocalDate.of(1991, 2, 20),
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
    @DisplayName("POST & GET & PATCH contacts flow")
    @WithMockUser(username = "ops", roles = {"OPERATIONS"})
    void testContactEndpointsFlow() throws Exception {
        String customerId = createTestCustomer();

        // 1. Add contact
        AddCustomerContactRequest request = new AddCustomerContactRequest();
        request.setContactType(ContactType.EMAIL);
        request.setContactValue("contact.user@neobank.com");
        request.setPrimary(true);

        String addResponse = mockMvc.perform(post("/api/v1/customers/{customerId}/contacts", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.contactValue").value("contact.user@neobank.com"))
                .andExpect(jsonPath("$.data.primary").value(true))
                .andExpect(jsonPath("$.data.verificationStatus").value("PENDING"))
                .andReturn().getResponse().getContentAsString();

        Long contactId = objectMapper.readTree(addResponse).path("data").path("id").asLong();

        // 2. Get contacts
        mockMvc.perform(get("/api/v1/customers/{customerId}/contacts", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(contactId));

        // 3. Verify contact
        mockMvc.perform(patch("/api/v1/customers/{customerId}/contacts/{contactId}/verify", customerId, contactId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.verificationStatus").value("VERIFIED"));

        // 4. Set primary contact
        mockMvc.perform(patch("/api/v1/customers/{customerId}/contacts/{contactId}/primary", customerId, contactId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.primary").value(true));

        // 5. Deactivate contact
        mockMvc.perform(patch("/api/v1/customers/{customerId}/contacts/{contactId}/deactivate", customerId, contactId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("INACTIVE"));
    }

    @Test
    @DisplayName("POST duplicate contact returns 409 Conflict")
    @WithMockUser(username = "ops", roles = {"OPERATIONS"})
    void testDuplicateContactConflict() throws Exception {
        String customerId = createTestCustomer();

        AddCustomerContactRequest request = new AddCustomerContactRequest();
        request.setContactType(ContactType.EMAIL);
        request.setContactValue("duplicate@neobank.com");
        request.setPrimary(false);

        mockMvc.perform(post("/api/v1/customers/{customerId}/contacts", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Duplicate submission
        mockMvc.perform(post("/api/v1/customers/{customerId}/contacts", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("CONTACT_ALREADY_EXISTS"));
    }
}
