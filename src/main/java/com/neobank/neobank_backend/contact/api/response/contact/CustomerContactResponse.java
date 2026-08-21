package com.neobank.neobank_backend.contact.api.response.contact;
import com.neobank.neobank_backend.contact.domain.contact.ContactStatus;
import com.neobank.neobank_backend.contact.domain.contact.ContactType;
import com.neobank.neobank_backend.contact.domain.contact.ContactVerificationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CustomerContactResponse {

    private final Long id;

    private final Long customerId;

    private final ContactType contactType;

    private final String contactValue;

    private final boolean primary;

    private final ContactVerificationStatus verificationStatus;

    private final ContactStatus status;

    private final LocalDateTime verifiedAt;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;
}
