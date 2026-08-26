package com.neobank.neobank_backend.preference;

import com.neobank.neobank_backend.common.api.ApiResponse;
import com.neobank.neobank_backend.preference.api.request.CreateCustomerPreferenceRequest;
import com.neobank.neobank_backend.preference.api.request.UpdateCustomerPreferenceRequest;
import com.neobank.neobank_backend.preference.api.respons.CustomerPreferenceResponse;
import com.neobank.neobank_backend.preference.application.CustomerPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/preferences")
@RequiredArgsConstructor
public class CustomerPreferenceController {

    private final CustomerPreferenceService customerPreferenceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATIONS','ADMIN')")
    public ResponseEntity<ApiResponse<CustomerPreferenceResponse>> createPreferences(
            @PathVariable UUID customerId,
            @Valid @RequestBody CreateCustomerPreferenceRequest request
    ) {
        CustomerPreferenceResponse response =
                customerPreferenceService.createPreferences(customerId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(MDC.get("correlationId"), response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERATIONS','ADMIN','AUDITOR')")
    public ResponseEntity<ApiResponse<CustomerPreferenceResponse>> getPreferences(
            @PathVariable UUID customerId
    ) {
        CustomerPreferenceResponse response =
                customerPreferenceService.getPreferences(customerId);

        return ResponseEntity.ok(
                ApiResponse.success(MDC.get("correlationId"), response)
        );
    }

    @PatchMapping
    @PreAuthorize("hasAnyRole('OPERATIONS','ADMIN')")
    public ResponseEntity<ApiResponse<CustomerPreferenceResponse>> updatePreferences(
            @PathVariable UUID customerId,
            @RequestBody UpdateCustomerPreferenceRequest request
    ) {
        CustomerPreferenceResponse response =
                customerPreferenceService.updatePreferences(customerId, request);

        return ResponseEntity.ok(
                ApiResponse.success(MDC.get("correlationId"), response)
        );
    }

    @PatchMapping("/deactivate")
    @PreAuthorize("hasAnyRole('OPERATIONS','ADMIN')")
    public ResponseEntity<ApiResponse<CustomerPreferenceResponse>> deactivatePreferences(
            @PathVariable UUID customerId
    ) {
        CustomerPreferenceResponse response =
                customerPreferenceService.deactivatePreferences(customerId);

        return ResponseEntity.ok(
                ApiResponse.success(MDC.get("correlationId"), response)
        );
    }
}
