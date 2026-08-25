package com.neobank.neobank_backend.preference.api.respons;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CustomerPreferenceResponse {

    private final Long id;

    private final Long customerId;

    private final PreferredLanguage preferredLanguage;

    private final CommunicationChannel preferredCommunicationChannel;

    private final Boolean marketingNotifications;

    private final Boolean transactionNotifications;

    private final Boolean securityNotifications;

    private final PreferenceStatus status;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;
}