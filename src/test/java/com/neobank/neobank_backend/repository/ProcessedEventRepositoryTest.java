package com.neobank.neobank_backend.repository;

import com.neobank.neobank_backend.lifecycle.event.consumer.ProcessedEvent;
import com.neobank.neobank_backend.lifecycle.event.consumer.ProcessedEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProcessedEventRepositoryTest {

    @Autowired
    private ProcessedEventRepository processedEventRepository;

    @Test
    @DisplayName("Should save processed event and enforce eventId uniqueness")
    void testProcessedEventRepository() {
        String eventId = UUID.randomUUID().toString();
        ProcessedEvent event1 = new ProcessedEvent(eventId, "customer.lifecycle.changed");
        processedEventRepository.saveAndFlush(event1);

        assertTrue(processedEventRepository.existsByEventId(eventId));

        ProcessedEvent duplicate = new ProcessedEvent(eventId, "customer.lifecycle.changed");
        assertThrows(DataIntegrityViolationException.class, () -> {
            processedEventRepository.saveAndFlush(duplicate);
        });
    }
}
