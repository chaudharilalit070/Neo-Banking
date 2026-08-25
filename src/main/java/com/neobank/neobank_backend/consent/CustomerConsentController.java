package com.neobank.neobank_backend.consent;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/consents")
@RequiredArgsConstructor
@Validated
public class CustomerConsentController {

    private final CustomerConsentService customerConsentService;


    /**
     * Grant customer consent.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerConsentResponse>> grantConsent(
            @PathVariable
            @Positive(message = "Customer ID must be positive")
            Long customerId,

            @Valid
            @RequestBody
            GrantCustomerConsentRequest request
    ) {

        CustomerConsentResponse response =
                customerConsentService.grantConsent(
                        customerId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Customer consent granted successfully",
                                response
                        )
                );
    }


    /**
     * Withdraw customer consent.
     */
    @PatchMapping("/withdraw")
    public ResponseEntity<ApiResponse<CustomerConsentResponse>> withdrawConsent(
            @PathVariable
            @Positive(message = "Customer ID must be positive")
            Long customerId,

            @Valid
            @RequestBody
            WithdrawCustomerConsentRequest request
    ) {

        CustomerConsentResponse response =
                customerConsentService.withdrawConsent(
                        customerId,
                        request
                );

        return ResponseEntity
                .ok(
                        ApiResponse.success(
                                "Customer consent withdrawn successfully",
                                response
                        )
                );
    }


    /**
     * Get complete consent history.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerConsentResponse>>>
    getConsentHistory(

            @PathVariable
            @Positive(message = "Customer ID must be positive")
            Long customerId
    ) {

        List<CustomerConsentResponse> response =
                customerConsentService.getConsentHistory(
                        customerId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Customer consent history retrieved successfully",
                        response
                )
        );
    }


    /**
     * Get consent history for a specific consent type.
     */
    @GetMapping("/{consentType}")
    public ResponseEntity<ApiResponse<List<CustomerConsentResponse>>>
    getConsentHistoryByType(

            @PathVariable
            @Positive(message = "Customer ID must be positive")
            Long customerId,

            @PathVariable
            ConsentType consentType
    ) {

        List<CustomerConsentResponse> response =
                customerConsentService.getConsentHistoryByType(
                        customerId,
                        consentType
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Customer consent history retrieved successfully",
                        response
                )
        );
    }


    /**
     * Get latest/current consent for a specific consent type.
     */
    @GetMapping("/{consentType}/latest")
    public ResponseEntity<ApiResponse<CustomerConsentResponse>>
    getLatestConsent(

            @PathVariable
            @Positive(message = "Customer ID must be positive")
            Long customerId,

            @PathVariable
            ConsentType consentType
    ) {

        CustomerConsentResponse response =
                customerConsentService.getLatestConsent(
                        customerId,
                        consentType
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Latest customer consent retrieved successfully",
                        response
                )
        );
    }
}