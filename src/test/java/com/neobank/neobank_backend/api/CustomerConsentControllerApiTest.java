package com.neobank.neobank_backend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobank.neobank_backend.consent.api.request.GrantCustomerConsentRequest;
import com.neobank.neobank_backend.consent.api.request.WithdrawCustomerConsentRequest;
import com.neobank.neobank_backend.consent.domain.ConsentSource;
import com.neobank.neobank_backend.consent.domain.ConsentType;
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
class CustomerConsentControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String createTestCustomer() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                CustomerType.INDIVIDUAL,
                "Consent",
                "Tester",
                "User",
                LocalDate.of(1996, 11, 11),
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
    @DisplayName("POST & GET & PATCH consent flow")
    @WithMockUser(username = "ops", roles = {"OPERATIONS"})
    void testConsentEndpointsFlow() throws Exception {
        String customerId = createTestCustomer();

        // 1. Grant Consent
        GrantCustomerConsentRequest grantReq = new GrantCustomerConsentRequest();
        grantReq.setConsentType(ConsentType.MARKETING_COMMUNICATION);
        grantReq.setConsentVersion("v1.0");
        grantReq.setConsentTextVersion("v1.0-marketing-notice");
        grantReq.setSource(ConsentSource.WEB);

        mockMvc.perform(post("/api/v1/customers/{customerId}/consents", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(grantReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("GRANTED"))
                .andExpect(jsonPath("$.data.consentType").value("MARKETING_COMMUNICATION"));

        // 2. Get Consent History
        mockMvc.perform(get("/api/v1/customers/{customerId}/consents", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1));

        // 3. Get Consent by Type
        mockMvc.perform(get("/api/v1/customers/{customerId}/consents/MARKETING_COMMUNICATION", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].consentVersion").value("v1.0"));

        // 4. Get Latest Consent
        mockMvc.perform(get("/api/v1/customers/{customerId}/consents/MARKETING_COMMUNICATION/latest", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("GRANTED"));

        // 5. Withdraw Consent
        WithdrawCustomerConsentRequest withdrawReq = new WithdrawCustomerConsentRequest();
        withdrawReq.setConsentType(ConsentType.MARKETING_COMMUNICATION);
        withdrawReq.setSource(ConsentSource.WEB);

        mockMvc.perform(patch("/api/v1/customers/{customerId}/consents/withdraw", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("WITHDRAWN"));
    }
}
