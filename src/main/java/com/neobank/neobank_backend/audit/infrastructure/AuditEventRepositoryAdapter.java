package com.neobank.neobank_backend.audit.infrastructure;

import com.neobank.neobank_backend.audit.domain.AuditEvent;
import com.neobank.neobank_backend.audit.domain.AuditEventRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class AuditEventRepositoryAdapter implements AuditEventRepository {

    private final SpringDataAuditEventRepository repository;

    public AuditEventRepositoryAdapter(
            SpringDataAuditEventRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public AuditEvent save(AuditEvent event) {
        JpaAuditEventEntity entity = toEntity(event);
        JpaAuditEventEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<AuditEvent> findByCustomerId(
            UUID customerId,
            LocalDateTime from,
            LocalDateTime to
    ) {
        return repository
                .findByCustomerIdAndOccurredAtBetweenOrderByOccurredAtDesc(
                        customerId,
                        from,
                        to
                )
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private JpaAuditEventEntity toEntity(AuditEvent event) {
        JpaAuditEventEntity entity = new JpaAuditEventEntity();
        entity.setCustomerId(event.getCustomerId());
        entity.setAction(event.getAction());
        entity.setPreviousStatus(event.getPreviousStatus());
        entity.setNewStatus(event.getNewStatus());
        entity.setReason(event.getReason());
        entity.setActorId(event.getActorId());
        entity.setActorType(event.getActorType());
        entity.setCorrelationId(event.getCorrelationId());
        entity.setOccurredAt(event.getOccurredAt());
        entity.setSource(event.getSource());
        return entity;
    }

    private AuditEvent toDomain(JpaAuditEventEntity entity) {
        return new AuditEvent(
                entity.getId(),
                entity.getCustomerId(),
                entity.getAction(),
                entity.getPreviousStatus(),
                entity.getNewStatus(),
                entity.getReason(),
                entity.getActorId(),
                entity.getActorType(),
                entity.getCorrelationId(),
                entity.getOccurredAt(),
                entity.getSource()
        );
    }
}
