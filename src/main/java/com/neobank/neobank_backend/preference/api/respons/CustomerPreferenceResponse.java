package com.neobank.neobank_backend.preference.api.respons;

import com.neobank.neobank_backend.preference.domain.CommunicationChannel;
import com.neobank.neobank_backend.preference.domain.PreferenceStatus;
import com.neobank.neobank_backend.preference.domain.PreferredLanguage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CustomerPreferenceResponse {

    private final Long id;

    private final UUID customerId;

    private final PreferredLanguage preferredLanguage;

    private final CommunicationChannel preferredCommunicationChannel;

    private final Boolean marketingNotifications;

    private final Boolean transactionNotifications;

    private final Boolean securityNotifications;

    private final PreferenceStatus status;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;
}
