package com.neobank.neobank_backend.preference.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateCustomerPreferenceRequest {

    @NotNull(message = "Preferred language is required")
    private PreferredLanguage preferredLanguage;

    @NotNull(message = "Preferred communication channel is required")
    private CommunicationChannel preferredCommunicationChannel;

    private Boolean marketingNotifications = false;

    private Boolean transactionNotifications = true;

    private Boolean securityNotifications = true;
}