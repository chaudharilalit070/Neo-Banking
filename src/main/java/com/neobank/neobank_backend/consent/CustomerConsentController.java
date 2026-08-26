package com.neobank.neobank_backend.consent;

import com.neobank.neobank_backend.common.api.ApiResponse;
import com.neobank.neobank_backend.consent.api.request.GrantCustomerConsentRequest;
import com.neobank.neobank_backend.consent.api.request.WithdrawCustomerConsentRequest;
import com.neobank.neobank_backend.consent.api.response.CustomerConsentResponse;
import com.neobank.neobank_backend.consent.application.CustomerConsentService;
import com.neobank.neobank_backend.consent.domain.ConsentType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/consents")
@RequiredArgsConstructor
@Validated
public class CustomerConsentController {

    private final CustomerConsentService customerConsentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATIONS','ADMIN')")
    public ResponseEntity<ApiResponse<CustomerConsentResponse>> grantConsent(
            @PathVariable UUID customerId,
            @Valid @RequestBody GrantCustomerConsentRequest request
    ) {
        CustomerConsentResponse response =
                customerConsentService.grantConsent(customerId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(MDC.get("correlationId"), response));
    }

    @PatchMapping("/withdraw")
    @PreAuthorize("hasAnyRole('OPERATIONS','ADMIN')")
    public ResponseEntity<ApiResponse<CustomerConsentResponse>> withdrawConsent(
            @PathVariable UUID customerId,
            @Valid @RequestBody WithdrawCustomerConsentRequest request
    ) {
        CustomerConsentResponse response =
                customerConsentService.withdrawConsent(customerId, request);

        return ResponseEntity.ok(
                ApiResponse.success(MDC.get("correlationId"), response)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERATIONS','ADMIN','AUDITOR')")
    public ResponseEntity<ApiResponse<List<CustomerConsentResponse>>> getConsentHistory(
            @PathVariable UUID customerId
    ) {
        List<CustomerConsentResponse> response =
                customerConsentService.getConsentHistory(customerId);

        return ResponseEntity.ok(
                ApiResponse.success(MDC.get("correlationId"), response)
        );
    }

    @GetMapping("/{consentType}")
    @PreAuthorize("hasAnyRole('OPERATIONS','ADMIN','AUDITOR')")
    public ResponseEntity<ApiResponse<List<CustomerConsentResponse>>> getConsentHistoryByType(
            @PathVariable UUID customerId,
            @PathVariable ConsentType consentType
    ) {
        List<CustomerConsentResponse> response =
                customerConsentService.getConsentHistoryByType(
                        customerId,
                        consentType
                );

        return ResponseEntity.ok(
                ApiResponse.success(MDC.get("correlationId"), response)
        );
    }

    @GetMapping("/{consentType}/latest")
    @PreAuthorize("hasAnyRole('OPERATIONS','ADMIN','AUDITOR')")
    public ResponseEntity<ApiResponse<CustomerConsentResponse>> getLatestConsent(
            @PathVariable UUID customerId,
            @PathVariable ConsentType consentType
    ) {
        CustomerConsentResponse response =
                customerConsentService.getLatestConsent(customerId, consentType);

        return ResponseEntity.ok(
                ApiResponse.success(MDC.get("correlationId"), response)
        );
    }
}
