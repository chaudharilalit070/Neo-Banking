package com.neobank.neobank_backend.consent.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WithdrawCustomerConsentRequest {

    @NotNull(message = "Consent type is required")
    private ConsentType consentType;

    @NotNull(message = "Consent source is required")
    private ConsentSource source;
}