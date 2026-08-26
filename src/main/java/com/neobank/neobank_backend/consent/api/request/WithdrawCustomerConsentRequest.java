package com.neobank.neobank_backend.consent.api.request;

import com.neobank.neobank_backend.consent.domain.ConsentSource;
import com.neobank.neobank_backend.consent.domain.ConsentType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WithdrawCustomerConsentRequest {

    @NotNull(message = "Consent type is required")
    private ConsentType consentType;

    @NotNull(message = "Consent source is required")
    private ConsentSource source;
}
