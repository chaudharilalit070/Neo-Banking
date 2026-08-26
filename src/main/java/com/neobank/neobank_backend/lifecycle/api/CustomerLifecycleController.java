package com.neobank.neobank_backend.lifecycle.api;

import com.neobank.neobank_backend.common.api.ApiResponse;
import com.neobank.neobank_backend.lifecycle.api.request.CustomerLifecycleActionRequest;
import com.neobank.neobank_backend.lifecycle.api.response.CustomerLifecycleResponse;
import com.neobank.neobank_backend.lifecycle.application.CustomerLifecycleService;
import jakarta.validation.Valid;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/lifecycle")
public class CustomerLifecycleController {

    private final CustomerLifecycleService lifecycleService;

    public CustomerLifecycleController(
            CustomerLifecycleService lifecycleService
    ) {
        this.lifecycleService = lifecycleService;
    }

    @PostMapping("/actions")
    @PreAuthorize("hasAnyRole('OPERATIONS', 'ADMIN')")
    public ResponseEntity<ApiResponse<CustomerLifecycleResponse>> applyAction(
            @PathVariable UUID customerId,
            @Valid @RequestBody CustomerLifecycleActionRequest request
    ) {
        CustomerLifecycleResponse response =
                lifecycleService.applyAction(customerId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(MDC.get("correlationId"), response));
    }

    @GetMapping("/current")
    @PreAuthorize("hasAnyRole('OPERATIONS', 'ADMIN', 'AUDITOR')")
    public ResponseEntity<ApiResponse<CustomerLifecycleResponse>> getCurrentLifecycle(
            @PathVariable UUID customerId
    ) {
        CustomerLifecycleResponse response =
                lifecycleService.getCurrentLifecycle(customerId);

        return ResponseEntity.ok(
                ApiResponse.success(MDC.get("correlationId"), response)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERATIONS', 'ADMIN', 'AUDITOR')")
    public ResponseEntity<ApiResponse<List<CustomerLifecycleResponse>>> getHistory(
            @PathVariable UUID customerId
    ) {
        List<CustomerLifecycleResponse> response =
                lifecycleService.getLifecycleHistory(customerId);

        return ResponseEntity.ok(
                ApiResponse.success(MDC.get("correlationId"), response)
        );
    }
}
