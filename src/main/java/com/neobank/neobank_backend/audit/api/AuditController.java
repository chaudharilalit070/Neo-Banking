package com.neobank.neobank_backend.audit.api;

import com.neobank.neobank_backend.audit.application.AuditService;
import com.neobank.neobank_backend.audit.domain.AuditEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @PreAuthorize("hasAnyRole('AUDITOR', 'OPERATIONS', 'ADMIN')")
    @GetMapping("/{customerId}/audit")
    public ResponseEntity<List<AuditEventResponse>> getAuditHistory(
            @PathVariable UUID customerId,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to
    ) {
        LocalDateTime fromDate =
                from != null ? from : LocalDateTime.now().minusDays(30);

        LocalDateTime toDate =
                to != null ? to : LocalDateTime.now();

        List<AuditEvent> events = auditService.getCustomerAuditHistory(
                customerId,
                fromDate,
                toDate
        );

        List<AuditEventResponse> response = events.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    private AuditEventResponse toResponse(AuditEvent event) {
        return new AuditEventResponse(
                event.getCustomerId(),
                event.getAction().name(),
                event.getPreviousStatus(),
                event.getNewStatus(),
                event.getReason(),
                event.getActorId(),
                event.getActorType(),
                event.getCorrelationId(),
                event.getOccurredAt(),
                event.getSource()
        );
    }
}
