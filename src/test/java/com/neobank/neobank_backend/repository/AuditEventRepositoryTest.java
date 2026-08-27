package com.neobank.neobank_backend.repository;

import com.neobank.neobank_backend.audit.domain.ActorType;
import com.neobank.neobank_backend.audit.domain.AuditAction;
import com.neobank.neobank_backend.audit.domain.AuditEvent;
import com.neobank.neobank_backend.audit.domain.AuditEventRepository;
import com.neobank.neobank_backend.customer.domain.Customer;
import com.neobank.neobank_backend.customer.domain.CustomerRepository;
import com.neobank.neobank_backend.customer.domain.CustomerType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuditEventRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AuditEventRepository auditEventRepository;

    @Test
    @DisplayName("Should save audit events and query by customerId and date range")
    void testAuditEventRepository() {
        Customer customer = customerRepository.save(Customer.create(
                "CUS-AUD-001",
                CustomerType.INDIVIDUAL,
                "Grace",
                null,
                "Hopper",
                LocalDate.of(1980, 12, 9),
                "USA",
                "test-user"
        ));

        AuditEvent event = new AuditEvent(
                customer.getId(),
                AuditAction.CUSTOMER_CREATED,
                null,
                "PROSPECT",
                "Initial Creation",
                "admin-1",
                ActorType.EMPLOYEE.name(),
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                "customer-service"
        );
        auditEventRepository.save(event);

        List<AuditEvent> history = auditEventRepository.findByCustomerId(
                customer.getId(),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );

        assertEquals(1, history.size());
        assertEquals(AuditAction.CUSTOMER_CREATED, history.get(0).getAction());
        assertEquals("admin-1", history.get(0).getActorId());
    }
}
