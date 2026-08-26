package com.neobank.neobank_backend.lifecycle.event.outbox;


public enum OutboxEventStatus {

    PENDING,

    PUBLISHED,

    FAILED
}