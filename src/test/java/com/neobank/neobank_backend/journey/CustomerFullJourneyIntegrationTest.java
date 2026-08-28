package com.neobank.neobank_backend.journey;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobank.neobank_backend.address.api.request.AddCustomerAddressRequest;
import com.neobank.neobank_backend.address.domain.AddressType;
import com.neobank.neobank_backend.consent.api.request.GrantCustomerConsentRequest;
import com.neobank.neobank_backend.consent.domain.ConsentSource;
import com.neobank.neobank_backend.consent.domain.ConsentType;
import com.neobank.neobank_backend.contact.api.request.contact.AddCustomerContactRequest;
import com.neobank.neobank_backend.contact.domain.contact.ContactType;
import com.neobank.neobank_backend.customer.api.request.CreateCustomerRequest;
import com.neobank.neobank_backend.customer.domain.CustomerType;
import com.neobank.neobank_backend.lifecycle.api.request.CustomerLifecycleActionRequest;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycleAction;
import com.neobank.neobank_backend.preference.api.request.CreateCustomerPreferenceRequest;
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
class CustomerFullJourneyIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Complete End-to-End Customer Lifecycle Journey via REST APIs")
    @WithMockUser(username = "super_admin", roles = {"ADMIN", "OPERATIONS", "AUDITOR"})
    void testCompleteCustomerJourney() throws Exception {
        // Step 1: Create Customer (PROSPECT)
        CreateCustomerRequest createRequest = new CreateCustomerRequest(
                CustomerType.INDIVIDUAL,
                "Alexander",
                "The",
                "Great",
                LocalDate.of(1990, 7, 20),
                "GRC"
        );

        String createResp = mockMvc.perform(post("/api/v1/customers")
                        .header("X-Correlation-Id", "CORR-JOURNEY-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.customerStatus").value("PROSPECT"))
                .andReturn().getResponse().getContentAsString();

        String customerId = objectMapper.readTree(createResp).path("data").path("id").asText();

        // Step 2: Add and Verify Contact
        AddCustomerContactRequest contactRequest = new AddCustomerContactRequest();
        contactRequest.setContactType(ContactType.EMAIL);
        contactRequest.setContactValue("alexander@macedon.com");
        contactRequest.setPrimary(true);

        String contactResp = mockMvc.perform(post("/api/v1/customers/{id}/contacts", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contactRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long contactId = objectMapper.readTree(contactResp).path("data").path("id").asLong();

        mockMvc.perform(patch("/api/v1/customers/{id}/contacts/{contactId}/verify", customerId, contactId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.verificationStatus").value("VERIFIED"));

        // Step 3: Add Residential Address
        AddCustomerAddressRequest addressRequest = new AddCustomerAddressRequest();
        addressRequest.setAddressType(AddressType.PERMANENT);
        addressRequest.setAddressLine1("1 Royal Palace Way");
        addressRequest.setCity("Pella");
        addressRequest.setState("Central Macedonia");
        addressRequest.setCountry("GRC");
        addressRequest.setPostalCode("58200");

        mockMvc.perform(post("/api/v1/customers/{id}/addresses", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        // Step 4: Configure Preferences
        CreateCustomerPreferenceRequest prefRequest = new CreateCustomerPreferenceRequest();
        prefRequest.setPreferredLanguage(PreferredLanguage.ENGLISH);
        prefRequest.setPreferredCommunicationChannel(CommunicationChannel.EMAIL);
        prefRequest.setMarketingNotifications(true);
        prefRequest.setTransactionNotifications(true);
        prefRequest.setSecurityNotifications(true);

        mockMvc.perform(post("/api/v1/customers/{id}/preferences", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prefRequest)))
                .andExpect(status().isCreated());

        // Step 5: Grant Consent
        GrantCustomerConsentRequest consentRequest = new GrantCustomerConsentRequest();
        consentRequest.setConsentType(ConsentType.DATA_PROCESSING);
        consentRequest.setConsentVersion("v2.1");
        consentRequest.setConsentTextVersion("v2.1-gdpr");
        consentRequest.setSource(ConsentSource.WEB);

        mockMvc.perform(post("/api/v1/customers/{id}/consents", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(consentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("GRANTED"));

        // Step 6: Start Onboarding -> ONBOARDING
        mockMvc.perform(post("/api/v1/customers/{id}/lifecycle/actions", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CustomerLifecycleActionRequest(CustomerLifecycleAction.START_ONBOARDING))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentStatus").value("ONBOARDING"));

        // Step 7: Complete Onboarding -> ACTIVE
        mockMvc.perform(post("/api/v1/customers/{id}/lifecycle/actions", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CustomerLifecycleActionRequest(CustomerLifecycleAction.COMPLETE_ONBOARDING))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentStatus").value("ACTIVE"));

        // Step 8: Deactivate -> INACTIVE
        mockMvc.perform(post("/api/v1/customers/{id}/lifecycle/actions", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CustomerLifecycleActionRequest(CustomerLifecycleAction.DEACTIVATE))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentStatus").value("INACTIVE"));

        // Step 9: Reactivate -> ACTIVE
        mockMvc.perform(post("/api/v1/customers/{id}/lifecycle/actions", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CustomerLifecycleActionRequest(CustomerLifecycleAction.REACTIVATE))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentStatus").value("ACTIVE"));

        // Step 10: Close -> CLOSED
        mockMvc.perform(post("/api/v1/customers/{id}/lifecycle/actions", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CustomerLifecycleActionRequest(CustomerLifecycleAction.CLOSE))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentStatus").value("CLOSED"));

        // Step 11: Verify Customer status is CLOSED in customer API
        mockMvc.perform(get("/api/v1/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.customerStatus").value("CLOSED"));

        // Step 12: Verify Audit Trail
        mockMvc.perform(get("/api/v1/customers/{id}/audit", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(6)); // CREATED + 5 lifecycle transitions

        // Step 13: Verify Lifecycle History
        mockMvc.perform(get("/api/v1/customers/{id}/lifecycle", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(6)); // INITIAL PROSPECT + 5 transitions
    }
}
