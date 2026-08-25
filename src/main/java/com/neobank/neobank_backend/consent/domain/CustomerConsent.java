package com.neobank.neobank_backend.consent.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "customer_consents",
        indexes = {
                @Index(
                        name = "idx_customer_consents_customer_id",
                        columnList = "customer_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerConsent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "customer_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_customer_consents_customer"
            )
    )
    private Customer customer;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "consent_type",
            nullable = false,
            length = 50
    )
    private ConsentType consentType;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private ConsentStatus status;


    @Column(
            name = "consent_version",
            nullable = false,
            length = 50
    )
    private String consentVersion;


    @Column(
            name = "consent_text_version",
            nullable = false,
            length = 50
    )
    private String consentTextVersion;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "source",
            nullable = false,
            length = 30
    )
    private ConsentSource source;


    @Column(name = "granted_at")
    private LocalDateTime grantedAt;


    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;


    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    @PrePersist
    protected void onCreate() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == ConsentStatus.GRANTED && grantedAt == null) {
            grantedAt = LocalDateTime.now();
        }

        if (status == ConsentStatus.WITHDRAWN && withdrawnAt == null) {
            withdrawnAt = LocalDateTime.now();
        }
    }
}