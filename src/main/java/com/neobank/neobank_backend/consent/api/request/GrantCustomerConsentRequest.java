package com.neobank.neobank_backend.consent.api.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GrantCustomerConsentRequest {

    @NotNull(message = "Consent type is required")
    private ConsentType consentType;

    @NotBlank(message = "Consent version is required")
    @Size(max = 50, message = "Consent version must not exceed 50 characters")
    private String consentVersion;

    @NotBlank(message = "Consent text version is required")
    @Size(max = 50, message = "Consent text version must not exceed 50 characters")
    private String consentTextVersion;

    @NotNull(message = "Consent source is required")
    private ConsentSource source;
}