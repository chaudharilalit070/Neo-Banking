package com.neobank.neobank_backend.lifecycle.infrastructure.persistence.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobank.neobank_backend.lifecycle.event.CustomerLifecycleChangedEvent;
import com.neobank.neobank_backend.lifecycle.event.consumer.ProcessedEvent;
import com.neobank.neobank_backend.lifecycle.event.consumer.ProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CustomerLifecycleEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(CustomerLifecycleEventConsumer.class);

    private final ProcessedEventRepository processedEventRepository;

    private final ObjectMapper objectMapper;

    public CustomerLifecycleEventConsumer(
            ProcessedEventRepository processedEventRepository,
            ObjectMapper objectMapper
    ) {
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @KafkaListener(
            topics = "customer.lifecycle.changed",
            groupId = "${spring.kafka.consumer.group-id:customer-service-group}"
    )
    public void consume(String payload) {
        try {
            CustomerLifecycleChangedEvent event =
                    objectMapper.readValue(
                            payload,
                            CustomerLifecycleChangedEvent.class
                    );

            if (processedEventRepository.existsByEventId(event.eventId())) {
                log.debug("Skipping already processed event {}", event.eventId());
                return;
            }

            processEvent(event);

            processedEventRepository.save(
                    new ProcessedEvent(event.eventId(), event.eventType())
            );
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to consume customer lifecycle event",
                    exception
            );
        }
    }

    private void processEvent(CustomerLifecycleChangedEvent event) {
        log.info(
                "Processing customer lifecycle event {} for customer {} -> {}",
                event.eventId(),
                event.customerId(),
                event.data().currentStatus()
        );
    }
}
