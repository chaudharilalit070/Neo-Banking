package com.neobank.neobank_backend.audit.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringDataAuditEventRepository
        extends JpaRepository<JpaAuditEventEntity, Long> {

    List<JpaAuditEventEntity>
    findByCustomerIdAndOccurredAtBetweenOrderByOccurredAtDesc(
            UUID customerId,
            LocalDateTime from,
            LocalDateTime to
    );
}
