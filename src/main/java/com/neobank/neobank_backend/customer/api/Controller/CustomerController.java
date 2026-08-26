package com.neobank.neobank_backend.customer.api.Controller;

import com.neobank.neobank_backend.common.api.ApiResponse;
import com.neobank.neobank_backend.customer.api.request.CreateCustomerRequest;
import com.neobank.neobank_backend.customer.api.request.UpdateCustomerRequest;
import com.neobank.neobank_backend.customer.api.respons.CustomerResponse;
import com.neobank.neobank_backend.customer.application.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATIONS', 'ADMIN')")
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request
    ) {

        CustomerResponse response =
                customerService.createCustomer(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                MDC.get("correlationId"),
                                response
                        )
                );
    }

    @GetMapping("/{customerId}")
    @PreAuthorize("hasAnyRole('OPERATIONS', 'ADMIN', 'AUDITOR')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(
            @PathVariable UUID customerId
    ) {

        CustomerResponse response =
                customerService.getCustomerById(customerId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        MDC.get("correlationId"),
                        response
                )
        );
    }

    @PutMapping("/{customerId}")
    @PreAuthorize("hasAnyRole('OPERATIONS', 'ADMIN')")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable UUID customerId,
            @Valid @RequestBody UpdateCustomerRequest request
    ) {

        CustomerResponse response =
                customerService.updateCustomer(
                        customerId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        MDC.get("correlationId"),
                        response
                )
        );
    }
}
