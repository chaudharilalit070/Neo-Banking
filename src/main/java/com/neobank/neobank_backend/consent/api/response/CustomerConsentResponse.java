package com.neobank.neobank_backend.consent.api.response;

import com.neobank.neobank_backend.consent.domain.ConsentSource;
import com.neobank.neobank_backend.consent.domain.ConsentStatus;
import com.neobank.neobank_backend.consent.domain.ConsentType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CustomerConsentResponse {

    private final Long id;

    private final UUID customerId;

    private final ConsentType consentType;

    private final ConsentStatus status;

    private final String consentVersion;

    private final String consentTextVersion;

    private final ConsentSource source;

    private final LocalDateTime grantedAt;

    private final LocalDateTime withdrawnAt;

    private final LocalDateTime createdAt;
}
