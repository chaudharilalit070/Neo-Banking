package com.neobank.neobank_backend.preference.domain;

import com.neobank.neobank_backend.customer.domain.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "customer_id",
            nullable = false,
            unique = true,
            columnDefinition = "CHAR(36)",
            foreignKey = @ForeignKey(name = "fk_customer_preferences_customer")
    )
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_language", nullable = false, length = 30)
    private PreferredLanguage preferredLanguage;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "preferred_communication_channel",
            nullable = false,
            length = 30
    )
    private CommunicationChannel preferredCommunicationChannel;

    @Column(name = "marketing_notifications", nullable = false)
    @Builder.Default
    private Boolean marketingNotifications = false;

    @Column(name = "transaction_notifications", nullable = false)
    @Builder.Default
    private Boolean transactionNotifications = true;

    @Column(name = "security_notifications", nullable = false)
    @Builder.Default
    private Boolean securityNotifications = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PreferenceStatus status = PreferenceStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
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
