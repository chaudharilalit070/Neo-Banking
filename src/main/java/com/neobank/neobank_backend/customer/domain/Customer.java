package com.neobank.neobank_backend.customer.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    @Id
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private UUID id;

    @Column(
            name = "customer_number",
            nullable = false,
            unique = true,
            length = 30
    )
    private String customerNumber;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "customer_type",
            nullable = false,
            length = 30
    )
    private CustomerType customerType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "customer_status",
            nullable = false,
            length = 30
    )
    private CustomerStatus customerStatus;

    @Column(
            name = "first_name",
            nullable = false,
            length = 100
    )
    private String firstName;

    @Column(
            name = "middle_name",
            length = 100
    )
    private String middleName;

    @Column(
            name = "last_name",
            nullable = false,
            length = 100
    )
    private String lastName;

    @Column(
            name = "date_of_birth",
            nullable = false
    )
    private LocalDate dateOfBirth;

    @Column(
            name = "nationality",
            length = 3
    )
    private String nationality;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @Column(
            name = "created_by",
            nullable = false,
            updatable = false,
            length = 100
    )
    private String createdBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(
            name = "updated_by",
            length = 100
    )
    private String updatedBy;

    @Version
    @Column(
            name = "version",
            nullable = false
    )
    private Long version;
}