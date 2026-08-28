package com.neobank.neobank_backend.transaction;

import com.neobank.neobank_backend.audit.domain.AuditEvent;
import com.neobank.neobank_backend.audit.domain.AuditEventRepository;
import com.neobank.neobank_backend.common.exception.BusinessException;
import com.neobank.neobank_backend.customer.api.request.CreateCustomerRequest;
import com.neobank.neobank_backend.customer.api.respons.CustomerResponse;
import com.neobank.neobank_backend.customer.application.CustomerService;
import com.neobank.neobank_backend.customer.domain.CustomerRepository;
import com.neobank.neobank_backend.customer.domain.CustomerStatus;
import com.neobank.neobank_backend.customer.domain.CustomerType;
import com.neobank.neobank_backend.lifecycle.api.request.CustomerLifecycleActionRequest;
import com.neobank.neobank_backend.lifecycle.application.CustomerLifecycleService;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycle;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycleAction;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycleRepository;
import com.neobank.neobank_backend.lifecycle.event.outbox.OutboxEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CustomerTransactionRollbackTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerLifecycleService lifecycleService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerLifecycleRepository lifecycleRepository;

    @Autowired
    private AuditEventRepository auditEventRepository;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Test
    @DisplayName("Failed lifecycle transition rolls back transaction without orphaned audit or outbox records")
    void testLifecycleFailureRollback() {
        // Create initial customer
        CustomerResponse customerResp = customerService.createCustomer(new CreateCustomerRequest(
                CustomerType.INDIVIDUAL,
                "Rollback",
                null,
                "Test",
                LocalDate.of(1990, 1, 1),
                "USA"
        ));

        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now().plusDays(1);

        List<AuditEvent> auditsBefore = auditEventRepository.findByCustomerId(customerResp.id(), from, to);
        List<CustomerLifecycle> lifecyclesBefore = lifecycleRepository.findAllByCustomerId(customerResp.id());
        int outboxCountBefore = outboxEventRepository.findAll().size();

        // Attempt illegal transition (DEACTIVATE from PROSPECT)
        assertThrows(BusinessException.class, () ->
                lifecycleService.applyAction(
                        customerResp.id(),
                        new CustomerLifecycleActionRequest(CustomerLifecycleAction.DEACTIVATE)
                )
        );

        // Verify status remains PROSPECT
        assertEquals(CustomerStatus.PROSPECT, customerRepository.findById(customerResp.id()).get().getCustomerStatus());

        // Verify no extra audit, outbox, or lifecycle records were committed
        List<AuditEvent> auditsAfter = auditEventRepository.findByCustomerId(customerResp.id(), from, to);
        List<CustomerLifecycle> lifecyclesAfter = lifecycleRepository.findAllByCustomerId(customerResp.id());
        int outboxCountAfter = outboxEventRepository.findAll().size();

        assertEquals(auditsBefore.size(), auditsAfter.size());
        assertEquals(lifecyclesBefore.size(), lifecyclesAfter.size());
        assertEquals(outboxCountBefore, outboxCountAfter);
    }
}
