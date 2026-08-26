package com.neobank.neobank_backend.lifecycle.event;


public interface CustomerLifecycleEventPublisher {

    void publish(CustomerLifecycleChangedEvent event);
}