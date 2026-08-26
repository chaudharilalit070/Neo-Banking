package com.neobank.neobank_backend.lifecycle.infrastructure.persistence.message;

import com.neobank.neobank_backend.lifecycle.event.CustomerLifecycleChangedEvent;
import com.neobank.neobank_backend.lifecycle.event.CustomerLifecycleEventPublisher;
import com.neobank.neobank_backend.lifecycle.event.outbox.OutboxEventService;
import org.springframework.stereotype.Component;

@Component
public class CustomerLifecycleEventPublisherAdapter
        implements CustomerLifecycleEventPublisher {

    private final OutboxEventService outboxEventService;

    public CustomerLifecycleEventPublisherAdapter(
            OutboxEventService outboxEventService
    ) {
        this.outboxEventService = outboxEventService;
    }

    @Override
    public void publish(CustomerLifecycleChangedEvent event) {
        outboxEventService.createLifecycleEvent(event);
    }
}
