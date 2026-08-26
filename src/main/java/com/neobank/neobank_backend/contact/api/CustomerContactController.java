package com.neobank.neobank_backend.contact.api;

import com.neobank.neobank_backend.common.api.ApiResponse;
import com.neobank.neobank_backend.contact.api.request.contact.AddCustomerContactRequest;
import com.neobank.neobank_backend.contact.api.response.contact.CustomerContactResponse;
import com.neobank.neobank_backend.contact.application.CustomerContactService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/contacts")
@RequiredArgsConstructor
public class CustomerContactController {

    private final CustomerContactService customerContactService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATIONS','ADMIN')")
    public ResponseEntity<ApiResponse<CustomerContactResponse>> addContact(
            @PathVariable UUID customerId,
            @Valid @RequestBody AddCustomerContactRequest request
    ) {
        CustomerContactResponse response =
                customerContactService.addContact(customerId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(MDC.get("correlationId"), response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERATIONS','ADMIN','AUDITOR')")
    public ResponseEntity<ApiResponse<List<CustomerContactResponse>>> getCustomerContacts(
            @PathVariable UUID customerId
    ) {
        List<CustomerContactResponse> response =
                customerContactService.getCustomerContacts(customerId);

        return ResponseEntity.ok(
                ApiResponse.success(MDC.get("correlationId"), response)
        );
    }

    @PatchMapping("/{contactId}/primary")
    @PreAuthorize("hasAnyRole('OPERATIONS','ADMIN')")
    public ResponseEntity<ApiResponse<CustomerContactResponse>> setPrimaryContact(
            @PathVariable UUID customerId,
            @PathVariable Long contactId
    ) {
        CustomerContactResponse response =
                customerContactService.setPrimaryContact(customerId, contactId);

        return ResponseEntity.ok(
                ApiResponse.success(MDC.get("correlationId"), response)
        );
    }

    @PatchMapping("/{contactId}/verify")
    @PreAuthorize("hasAnyRole('OPERATIONS','ADMIN')")
    public ResponseEntity<ApiResponse<CustomerContactResponse>> verifyContact(
            @PathVariable UUID customerId,
            @PathVariable Long contactId
    ) {
        CustomerContactResponse response =
                customerContactService.verifyContact(customerId, contactId);

        return ResponseEntity.ok(
                ApiResponse.success(MDC.get("correlationId"), response)
        );
    }

    @PatchMapping("/{contactId}/deactivate")
    @PreAuthorize("hasAnyRole('OPERATIONS','ADMIN')")
    public ResponseEntity<ApiResponse<CustomerContactResponse>> deactivateContact(
            @PathVariable UUID customerId,
            @PathVariable Long contactId
    ) {
        CustomerContactResponse response =
                customerContactService.deactivateContact(customerId, contactId);

        return ResponseEntity.ok(
                ApiResponse.success(MDC.get("correlationId"), response)
        );
    }
}
