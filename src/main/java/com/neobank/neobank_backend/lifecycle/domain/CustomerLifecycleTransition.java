package com.neobank.neobank_backend.lifecycle.domain;

public record CustomerLifecycleTransition(

        CustomerLifecycleStatus newStatus,

        CustomerLifecycleReason reason

) {
}