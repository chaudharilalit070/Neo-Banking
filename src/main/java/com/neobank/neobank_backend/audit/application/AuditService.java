package com.neobank.neobank_backend.audit.application;

import com.neobank.neobank_backend.audit.domain.ActorType;
import com.neobank.neobank_backend.audit.domain.AuditAction;
import com.neobank.neobank_backend.audit.domain.AuditEvent;
import com.neobank.neobank_backend.audit.domain.AuditEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuditService {

    private static final String SERVICE_SOURCE = "customer-service";

    private final AuditEventRepository auditEventRepository;

    public AuditService(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @Transactional
    public void recordCustomerCreation(
            UUID customerId,
            String actorId,
            String actorType,
            String correlationId
    ) {
        AuditEvent event = new AuditEvent(
                customerId,
                AuditAction.CUSTOMER_CREATED,
                null,
                "PROSPECT",
                "Customer profile created",
                actorId,
                actorType != null ? actorType : ActorType.EMPLOYEE.name(),
                correlationId,
                LocalDateTime.now(),
                SERVICE_SOURCE
        );

        auditEventRepository.save(event);
    }

    @Transactional
    public void recordCustomerUpdate(
            UUID customerId,
            String actorId,
            String actorType,
            String correlationId
    ) {
        AuditEvent event = new AuditEvent(
                customerId,
                AuditAction.CUSTOMER_UPDATED,
                null,
                null,
                "Customer profile updated",
                actorId,
                actorType != null ? actorType : ActorType.EMPLOYEE.name(),
                correlationId,
                LocalDateTime.now(),
                SERVICE_SOURCE
        );

        auditEventRepository.save(event);
    }

    @Transactional
    public void recordLifecycleChange(
            UUID customerId,
            String previousStatus,
            String newStatus,
            String reason,
            String actorId,
            String actorType,
            String correlationId
    ) {
        AuditEvent event = new AuditEvent(
                customerId,
                AuditAction.CUSTOMER_LIFECYCLE_CHANGED,
                previousStatus,
                newStatus,
                reason,
                actorId,
                actorType != null ? actorType : ActorType.EMPLOYEE.name(),
                correlationId,
                LocalDateTime.now(),
                SERVICE_SOURCE
        );

        auditEventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<AuditEvent> getCustomerAuditHistory(
            UUID customerId,
            LocalDateTime from,
            LocalDateTime to
    ) {
        return auditEventRepository.findByCustomerId(
                customerId,
                from,
                to
        );
    }
}
