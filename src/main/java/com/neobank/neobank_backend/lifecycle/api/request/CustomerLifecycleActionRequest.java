package com.neobank.neobank_backend.lifecycle.api.request;

import com.neobank.neobank_backend.lifecycle.domain.CustomerLifecycleAction;
import jakarta.validation.constraints.NotNull;

public record CustomerLifecycleActionRequest(

        @NotNull(message = "Lifecycle action is required")
        CustomerLifecycleAction action

) {
}
