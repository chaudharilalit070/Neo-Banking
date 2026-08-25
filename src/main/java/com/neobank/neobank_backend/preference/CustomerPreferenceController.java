package com.neobank.neobank_backend.preference;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/preferences")
@RequiredArgsConstructor
public class CustomerPreferenceController {

    private final CustomerPreferenceService customerPreferenceService;


    /**
     * Create customer preferences.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CustomerPreferenceResponse> createPreferences(
            @PathVariable Long customerId,
            @Valid @RequestBody CreateCustomerPreferenceRequest request
    ) {

        CustomerPreferenceResponse response =
                customerPreferenceService.createPreferences(
                        customerId,
                        request
                );

        return ApiResponse.success(
                "Customer preferences created successfully",
                response
        );
    }


    /**
     * Get customer preferences.
     */
    @GetMapping
    public ApiResponse<CustomerPreferenceResponse> getPreferences(
            @PathVariable Long customerId
    ) {

        CustomerPreferenceResponse response =
                customerPreferenceService.getPreferences(customerId);

        return ApiResponse.success(
                "Customer preferences retrieved successfully",
                response
        );
    }


    /**
     * Partially update customer preferences.
     */
    @PatchMapping
    public ApiResponse<CustomerPreferenceResponse> updatePreferences(
            @PathVariable Long customerId,
            @RequestBody UpdateCustomerPreferenceRequest request
    ) {

        CustomerPreferenceResponse response =
                customerPreferenceService.updatePreferences(
                        customerId,
                        request
                );

        return ApiResponse.success(
                "Customer preferences updated successfully",
                response
        );
    }


    /**
     * Deactivate customer preferences.
     */
    @PatchMapping("/deactivate")
    public ApiResponse<CustomerPreferenceResponse> deactivatePreferences(
            @PathVariable Long customerId
    ) {

        CustomerPreferenceResponse response =
                customerPreferenceService.deactivatePreferences(customerId);

        return ApiResponse.success(
                "Customer preferences deactivated successfully",
                response
        );
    }
}