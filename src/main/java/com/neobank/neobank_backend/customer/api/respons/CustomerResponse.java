package com.neobank.neobank_backend.customer.api.respons;


import com.neobank.neobank_backend.customer.domain.CustomerStatus;
import com.neobank.neobank_backend.customer.domain.CustomerType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CustomerResponse(

        UUID id,
        String customerNumber,
        CustomerType customerType,
        CustomerStatus customerStatus,

        String firstName,
        String middleName,
        String lastName,

        LocalDate dateOfBirth,
        String nationality,

        Instant createdAt,
        Instant updatedAt

) {
}