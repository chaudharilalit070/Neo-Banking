package com.neobank.neobank_backend.lifecycle.domain;


import com.company.neobanking.customer.customer.domain.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "customer_lifecycle",
        indexes = {
                @Index(
                        name = "idx_customer_lifecycle_customer_id",
                        columnList = "customer_id"
                ),
                @Index(
                        name = "idx_customer_lifecycle_customer_status",
                        columnList = "customer_id,current_status"
                ),
                @Index(
                        name = "idx_customer_lifecycle_effective_at",
                        columnList = "customer_id,effective_at"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CustomerLifecycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "customer_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_customer_lifecycle_customer"
            )
    )
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "previous_status",
            length = 30
    )
    private CustomerLifecycleStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "current_status",
            nullable = false,
            length = 30
    )
    private CustomerLifecycleStatus currentStatus;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "reason",
            nullable = false,
            length = 50
    )
    private CustomerLifecycleReason reason;

    @Column(
            name = "effective_at",
            nullable = false
    )
    private LocalDateTime effectiveAt;

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

        if (effectiveAt == null) {
            effectiveAt = now;
        }

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }


    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }
}