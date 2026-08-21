package com.neobank.neobank_backend.address.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "customer_addresses",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_customer_address_type",
                        columnNames = {
                                "customer_id",
                                "address_type"
                        }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "customer_id",
            nullable = false
    )
    private Customer customer;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "address_type",
            nullable = false,
            length = 30
    )
    private AddressType addressType;


    @Column(
            name = "address_line1",
            nullable = false,
            length = 255
    )
    private String addressLine1;


    @Column(
            name = "address_line2",
            length = 255
    )
    private String addressLine2;


    @Column(
            name = "landmark",
            length = 255
    )
    private String landmark;


    @Column(
            name = "city",
            nullable = false,
            length = 100
    )
    private String city;


    @Column(
            name = "district",
            length = 100
    )
    private String district;


    @Column(
            name = "state",
            nullable = false,
            length = 100
    )
    private String state;


    @Column(
            name = "country",
            nullable = false,
            length = 100
    )
    private String country;


    @Column(
            name = "postal_code",
            nullable = false,
            length = 20
    )
    private String postalCode;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private AddressStatus status;


    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = AddressStatus.ACTIVE;
        }
    }


    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}