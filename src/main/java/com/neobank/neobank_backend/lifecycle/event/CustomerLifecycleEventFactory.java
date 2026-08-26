package com.neobank.neobank_backend.lifecycle.event;

import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycle;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CustomerLifecycleEventFactory {

    private static final String EVENT_TYPE = "customer.lifecycle.changed";

    private static final int EVENT_VERSION = 1;

    private static final String SOURCE = "customer-service";

    public CustomerLifecycleChangedEvent create(
            CustomerLifecycle lifecycle,
            String correlationId
    ) {
        CustomerLifecycleEventData data = new CustomerLifecycleEventData(
                lifecycle.getId(),
                lifecycle.getPreviousStatus(),
                lifecycle.getCurrentStatus(),
                lifecycle.getReason(),
                lifecycle.getEffectiveAt()
        );

        return new CustomerLifecycleChangedEvent(
                UUID.randomUUID().toString(),
                EVENT_TYPE,
                EVENT_VERSION,
                LocalDateTime.now(),
                SOURCE,
                correlationId,
                lifecycle.getCustomer().getId(),
                data
        );
    }
}
