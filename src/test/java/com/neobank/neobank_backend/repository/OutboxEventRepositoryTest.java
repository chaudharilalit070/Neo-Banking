package com.neobank.neobank_backend.repository;

import com.neobank.neobank_backend.lifecycle.event.outbox.OutboxEvent;
import com.neobank.neobank_backend.lifecycle.event.outbox.OutboxEventRepository;
import com.neobank.neobank_backend.lifecycle.event.outbox.OutboxEventStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OutboxEventRepositoryTest {

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Test
    @DisplayName("Should save outbox events, query pending events, and update status")
    void testOutboxEventRepository() {
        OutboxEvent event = new OutboxEvent(
                "CUSTOMER",
                UUID.randomUUID().toString(),
                "customer.lifecycle.changed",
                "{\"status\":\"ACTIVE\"}"
        );

        OutboxEvent saved = outboxEventRepository.save(event);
        assertNotNull(saved.getId());
        assertEquals(OutboxEventStatus.PENDING, saved.getStatus());

        List<OutboxEvent> pendingList = outboxEventRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxEventStatus.PENDING);
        assertFalse(pendingList.isEmpty());

        saved.markPublished();
        outboxEventRepository.save(saved);

        assertEquals(OutboxEventStatus.PUBLISHED, saved.getStatus());
        assertNotNull(saved.getPublishedAt());
    }
}
