package com.neobank.neobank_backend.messaging;

import com.neobank.neobank_backend.lifecycle.event.outbox.OutboxEvent;
import com.neobank.neobank_backend.lifecycle.event.outbox.OutboxEventRepository;
import com.neobank.neobank_backend.lifecycle.event.outbox.OutboxEventStatus;
import com.neobank.neobank_backend.lifecycle.infrastructure.persistence.message.CustomerLifecycleOutboxPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CustomerLifecycleOutboxPublisherTest {

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private CustomerLifecycleOutboxPublisher outboxPublisher;

    @Test
    @DisplayName("Should publish pending outbox events and mark them PUBLISHED")
    void testPublishPendingEventsSuccess() {
        OutboxEvent event = new OutboxEvent(
                "CUSTOMER",
                UUID.randomUUID().toString(),
                "customer.lifecycle.changed",
                "{\"status\":\"ACTIVE\"}"
        );
        OutboxEvent saved = outboxEventRepository.save(event);

        SendResult<String, String> mockSendResult = mock(SendResult.class);
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(mockSendResult);
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(future);

        outboxPublisher.publishPendingEvents();

        OutboxEvent updated = outboxEventRepository.findById(saved.getId()).orElseThrow();
        assertEquals(OutboxEventStatus.PUBLISHED, updated.getStatus());
        assertNotNull(updated.getPublishedAt());
    }

    @Test
    @DisplayName("Should mark outbox event FAILED and increment retry count on kafka failure")
    void testPublishPendingEventsFailure() {
        OutboxEvent event = new OutboxEvent(
                "CUSTOMER",
                UUID.randomUUID().toString(),
                "customer.lifecycle.changed",
                "{\"status\":\"INACTIVE\"}"
        );
        OutboxEvent saved = outboxEventRepository.save(event);

        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka broker down"));
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(future);

        outboxPublisher.publishPendingEvents();

        OutboxEvent updated = outboxEventRepository.findById(saved.getId()).orElseThrow();
        assertEquals(OutboxEventStatus.FAILED, updated.getStatus());
        assertEquals(1, updated.getRetryCount());
    }
}
