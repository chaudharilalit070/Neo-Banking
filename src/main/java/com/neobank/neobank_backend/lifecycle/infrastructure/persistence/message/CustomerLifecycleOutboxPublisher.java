package com.neobank.neobank_backend.lifecycle.infrastructure.persistence.message;

import com.neobank.neobank_backend.lifecycle.event.outbox.OutboxEvent;
import com.neobank.neobank_backend.lifecycle.event.outbox.OutboxEventRepository;
import com.neobank.neobank_backend.lifecycle.event.outbox.OutboxEventStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CustomerLifecycleOutboxPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(CustomerLifecycleOutboxPublisher.class);

    private static final String TOPIC = "customer.lifecycle.changed";

    private final OutboxEventRepository outboxEventRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public CustomerLifecycleOutboxPublisher(
            OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${outbox.publisher.delay-ms:5000}")
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> events =
                outboxEventRepository.findTop100ByStatusOrderByCreatedAtAsc(
                        OutboxEventStatus.PENDING
                );

        for (OutboxEvent event : events) {
            try {
                kafkaTemplate.send(
                        TOPIC,
                        event.getAggregateId(),
                        event.getPayload()
                ).get();

                event.markPublished();
                outboxEventRepository.save(event);
            } catch (Exception exception) {
                log.warn(
                        "Failed to publish outbox event id={}",
                        event.getId(),
                        exception
                );
                event.markFailed();
                outboxEventRepository.save(event);
            }
        }
    }
}
