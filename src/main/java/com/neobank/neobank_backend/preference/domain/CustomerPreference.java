package com.neobank.neobank_backend.preference.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "customer_preferences",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_customer_preferences_customer_id",
                        columnNames = "customer_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /**
     * One customer has one preference profile.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "customer_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(
                    name = "fk_customer_preferences_customer"
            )
    )
    private Customer customer;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "preferred_language",
            nullable = false,
            length = 30
    )
    private PreferredLanguage preferredLanguage;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "preferred_communication_channel",
            nullable = false,
            length = 30
    )
    private CommunicationChannel preferredCommunicationChannel;


    @Column(
            name = "marketing_notifications",
            nullable = false
    )
    @Builder.Default
    private Boolean marketingNotifications = false;


    @Column(
            name = "transaction_notifications",
            nullable = false
    )
    @Builder.Default
    private Boolean transactionNotifications = true;


    @Column(
            name = "security_notifications",
            nullable = false
    )
    @Builder.Default
    private Boolean securityNotifications = true;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    @Builder.Default
    private PreferenceStatus status = PreferenceStatus.ACTIVE;


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
    public void prePersist() {

        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.marketingNotifications == null) {
            this.marketingNotifications = false;
        }

        if (this.transactionNotifications == null) {
            this.transactionNotifications = true;
        }

        if (this.securityNotifications == null) {
            this.securityNotifications = true;
        }

        if (this.status == null) {
            this.status = PreferenceStatus.ACTIVE;
        }
    }


    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}