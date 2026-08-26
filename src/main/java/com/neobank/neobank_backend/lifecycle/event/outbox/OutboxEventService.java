package com.neobank.neobank_backend.lifecycle.event.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobank.neobank_backend.lifecycle.event.CustomerLifecycleChangedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxEventService {

    private static final String AGGREGATE_TYPE = "CUSTOMER";

    private static final String EVENT_TYPE = "customer.lifecycle.changed";

    private final OutboxEventRepository outboxEventRepository;

    private final ObjectMapper objectMapper;

    public OutboxEventService(
            OutboxEventRepository outboxEventRepository,
            ObjectMapper objectMapper
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OutboxEvent createLifecycleEvent(CustomerLifecycleChangedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent = new OutboxEvent(
                    AGGREGATE_TYPE,
                    event.customerId().toString(),
                    EVENT_TYPE,
                    payload
            );

            return outboxEventRepository.save(outboxEvent);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Unable to serialize customer lifecycle event",
                    exception
            );
        }
    }
}
