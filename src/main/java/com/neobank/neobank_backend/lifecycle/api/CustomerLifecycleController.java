package com.neobank.neobank_backend.lifecycle.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/lifecycle")
public class CustomerLifecycleController {

    private final CustomerLifecycleService lifecycleService;

    public CustomerLifecycleController(
            CustomerLifecycleService lifecycleService
    ) {
        this.lifecycleService = lifecycleService;
    }

    /**
     * Apply a lifecycle action to a customer.
     */
    @PostMapping("/actions")
    public ResponseEntity<ApiResponse<CustomerLifecycleResponse>> applyAction(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerLifecycleActionRequest request
    ) {

        CustomerLifecycleResponse response =
                lifecycleService.applyAction(
                        customerId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }

    /**
     * Get the customer's current lifecycle.
     */
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<CustomerLifecycleResponse>> getCurrentLifecycle(
            @PathVariable Long customerId
    ) {

        CustomerLifecycleResponse response =
                lifecycleService.getCurrentLifecycle(customerId);

        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }

    /**
     * Get complete lifecycle history.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerLifecycleResponse>>> getHistory(
            @PathVariable Long customerId
    ) {

        List<CustomerLifecycleResponse> response =
                lifecycleService.getLifecycleHistory(customerId);

        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }
}