package com.neobank.neobank_backend.address.api.response;

import com.neobank.neobank_backend.address.domain.AddressStatus;
import com.neobank.neobank_backend.address.domain.AddressType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CustomerAddressResponse {

    private final Long id;

    private final UUID customerId;

    private final AddressType addressType;

    private final String addressLine1;

    private final String addressLine2;

    private final String landmark;

    private final String city;

    private final String district;

    private final String state;

    private final String country;

    private final String postalCode;

    private final AddressStatus status;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;
}
