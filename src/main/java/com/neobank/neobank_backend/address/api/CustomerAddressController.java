package com.neobank.neobank_backend.address.api;

import com.neobank.neobank_backend.address.api.request.AddCustomerAddressRequest;
import com.neobank.neobank_backend.address.api.response.CustomerAddressResponse;
import com.neobank.neobank_backend.address.application.CustomerAddressService;
import com.neobank.neobank_backend.common.api.ApiResponse;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/addresses")
@RequiredArgsConstructor
public class CustomerAddressController {

    private final CustomerAddressService customerAddressService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATIONS','ADMIN')")
    public ResponseEntity<ApiResponse<CustomerAddressResponse>> addAddress(
            @PathVariable UUID customerId,
            @Valid @RequestBody AddCustomerAddressRequest request
    ) {
        CustomerAddressResponse response =
                customerAddressService.addAddress(customerId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(MDC.get("correlationId"), response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERATIONS','ADMIN','AUDITOR')")
    public ResponseEntity<ApiResponse<List<CustomerAddressResponse>>> getCustomerAddresses(
            @PathVariable UUID customerId
    ) {
        List<CustomerAddressResponse> response =
                customerAddressService.getCustomerAddresses(customerId);

        return ResponseEntity.ok(
                ApiResponse.success(MDC.get("correlationId"), response)
        );
    }

    @GetMapping("/{addressId}")
    @PreAuthorize("hasAnyRole('OPERATIONS','ADMIN','AUDITOR')")
    public ResponseEntity<ApiResponse<CustomerAddressResponse>> getAddressById(
            @PathVariable UUID customerId,
            @PathVariable Long addressId
    ) {
        CustomerAddressResponse response =
                customerAddressService.getAddressById(customerId, addressId);

        return ResponseEntity.ok(
                ApiResponse.success(MDC.get("correlationId"), response)
        );
    }

    @PutMapping("/{addressId}")
    @PreAuthorize("hasAnyRole('OPERATIONS','ADMIN')")
    public ResponseEntity<ApiResponse<CustomerAddressResponse>> updateAddress(
            @PathVariable UUID customerId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddCustomerAddressRequest request
    ) {
        CustomerAddressResponse response =
                customerAddressService.updateAddress(customerId, addressId, request);

        return ResponseEntity.ok(
                ApiResponse.success(MDC.get("correlationId"), response)
        );
    }

    @PatchMapping("/{addressId}/deactivate")
    @PreAuthorize("hasAnyRole('OPERATIONS','ADMIN')")
    public ResponseEntity<ApiResponse<CustomerAddressResponse>> deactivateAddress(
            @PathVariable UUID customerId,
            @PathVariable Long addressId
    ) {
        CustomerAddressResponse response =
                customerAddressService.deactivateAddress(customerId, addressId);

        return ResponseEntity.ok(
                ApiResponse.success(MDC.get("correlationId"), response)
        );
    }
}
