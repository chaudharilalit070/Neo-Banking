package com.neobank.neobank_backend.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycleReason;
import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycleStatus;
import com.neobank.neobank_backend.lifecycle.event.CustomerLifecycleChangedEvent;
import com.neobank.neobank_backend.lifecycle.event.CustomerLifecycleEventData;
import com.neobank.neobank_backend.lifecycle.event.consumer.ProcessedEventRepository;
import com.neobank.neobank_backend.lifecycle.infrastructure.persistence.message.CustomerLifecycleEventConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CustomerLifecycleConsumerIdempotencyTest {

    @Autowired
    private CustomerLifecycleEventConsumer consumer;

    @Autowired
    private ProcessedEventRepository processedEventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should consume event and prevent duplicate processing on replay")
    void testConsumerIdempotency() throws Exception {
        String eventId = UUID.randomUUID().toString();
        UUID customerId = UUID.randomUUID();

        CustomerLifecycleEventData data = new CustomerLifecycleEventData(
                1L,
                CustomerLifecycleStatus.PROSPECT,
                CustomerLifecycleStatus.ONBOARDING,
                CustomerLifecycleReason.ONBOARDING_STARTED,
                LocalDateTime.now()
        );

        CustomerLifecycleChangedEvent event = new CustomerLifecycleChangedEvent(
                eventId,
                "customer.lifecycle.changed",
                1,
                LocalDateTime.now(),
                "customer-service",
                UUID.randomUUID().toString(),
                customerId,
                data
        );

        String payload = objectMapper.writeValueAsString(event);

        // First consume
        assertFalse(processedEventRepository.existsByEventId(eventId));
        consumer.consume(payload);
        assertTrue(processedEventRepository.existsByEventId(eventId));

        long countBefore = processedEventRepository.count();

        // Duplicate consume (Replay)
        consumer.consume(payload);
        long countAfter = processedEventRepository.count();

        // Count should not increase
        assertEquals(countBefore, countAfter);
    }
}
