package com.neobank.neobank_backend.audit.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditEventRepository {

    AuditEvent save(AuditEvent auditEvent);

    List<AuditEvent> findByCustomerId(
            UUID customerId,
            LocalDateTime from,
            LocalDateTime to
    );
}
