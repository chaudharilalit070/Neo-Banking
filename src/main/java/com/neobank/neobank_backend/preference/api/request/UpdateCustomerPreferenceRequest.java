package com.neobank.neobank_backend.preference.api.request;

import com.neobank.neobank_backend.preference.domain.CommunicationChannel;
import com.neobank.neobank_backend.preference.domain.PreferredLanguage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateCustomerPreferenceRequest {

    private PreferredLanguage preferredLanguage;

    private CommunicationChannel preferredCommunicationChannel;

    private Boolean marketingNotifications;

    private Boolean transactionNotifications;

    private Boolean securityNotifications;
}
