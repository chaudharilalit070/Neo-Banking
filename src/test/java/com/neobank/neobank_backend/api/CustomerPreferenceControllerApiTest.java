package com.neobank.neobank_backend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobank.neobank_backend.customer.api.request.CreateCustomerRequest;
import com.neobank.neobank_backend.customer.domain.CustomerType;
import com.neobank.neobank_backend.preference.api.request.CreateCustomerPreferenceRequest;
import com.neobank.neobank_backend.preference.api.request.UpdateCustomerPreferenceRequest;
import com.neobank.neobank_backend.preference.domain.CommunicationChannel;
import com.neobank.neobank_backend.preference.domain.PreferredLanguage;
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
class CustomerPreferenceControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String createTestCustomer() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                CustomerType.INDIVIDUAL,
                "Pref",
                "Tester",
                "User",
                LocalDate.of(1994, 6, 25),
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
    @DisplayName("POST & GET & PATCH preferences flow")
    @WithMockUser(username = "ops", roles = {"OPERATIONS"})
    void testPreferenceEndpointsFlow() throws Exception {
        String customerId = createTestCustomer();

        // 1. Create Preferences
        CreateCustomerPreferenceRequest createReq = new CreateCustomerPreferenceRequest();
        createReq.setPreferredLanguage(PreferredLanguage.EN);
        createReq.setPreferredCommunicationChannel(CommunicationChannel.EMAIL);
        createReq.setMarketingNotifications(false);
        createReq.setTransactionNotifications(true);
        createReq.setSecurityNotifications(true);

        mockMvc.perform(post("/api/v1/customers/{customerId}/preferences", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.preferredLanguage").value("EN"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        // 2. Get Preferences
        mockMvc.perform(get("/api/v1/customers/{customerId}/preferences", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.preferredCommunicationChannel").value("EMAIL"));

        // 3. Update Preferences
        UpdateCustomerPreferenceRequest updateReq = new UpdateCustomerPreferenceRequest();
        updateReq.setPreferredCommunicationChannel(CommunicationChannel.SMS);
        updateReq.setMarketingNotifications(true);

        mockMvc.perform(patch("/api/v1/customers/{customerId}/preferences", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.preferredCommunicationChannel").value("SMS"))
                .andExpect(jsonPath("$.data.marketingNotifications").value(true));

        // 4. Deactivate Preferences
        mockMvc.perform(patch("/api/v1/customers/{customerId}/preferences/deactivate", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("INACTIVE"));
    }
}
