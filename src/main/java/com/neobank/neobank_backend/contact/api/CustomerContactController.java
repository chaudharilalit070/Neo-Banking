package com.neobank.neobank_backend.contact.api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/contacts")
@RequiredArgsConstructor
public class CustomerContactController {

    private final CustomerContactService customerContactService;


    @PostMapping
    public ResponseEntity<ApiResponse<CustomerContactResponse>> addContact(
            @PathVariable Long customerId,
            @Valid @RequestBody AddCustomerContactRequest request
    ) {

        CustomerContactResponse response =
                customerContactService.addContact(customerId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Customer contact added successfully",
                                response
                        )
                );
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerContactResponse>>>
    getCustomerContacts(
            @PathVariable Long customerId
    ) {

        List<CustomerContactResponse> response =
                customerContactService.getCustomerContacts(customerId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Customer contacts retrieved successfully",
                        response
                )
        );
    }


    @PatchMapping("/{contactId}/primary")
    public ResponseEntity<ApiResponse<CustomerContactResponse>>
    setPrimaryContact(
            @PathVariable Long customerId,
            @PathVariable Long contactId
    ) {

        CustomerContactResponse response =
                customerContactService.setPrimaryContact(
                        customerId,
                        contactId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Primary contact updated successfully",
                        response
                )
        );
    }


    @PatchMapping("/{contactId}/verify")
    public ResponseEntity<ApiResponse<CustomerContactResponse>>
    verifyContact(
            @PathVariable Long customerId,
            @PathVariable Long contactId
    ) {

        CustomerContactResponse response =
                customerContactService.verifyContact(
                        customerId,
                        contactId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Customer contact verified successfully",
                        response
                )
        );
    }


    @PatchMapping("/{contactId}/deactivate")
    public ResponseEntity<ApiResponse<CustomerContactResponse>>
    deactivateContact(
            @PathVariable Long customerId,
            @PathVariable Long contactId
    ) {

        CustomerContactResponse response =
                customerContactService.deactivateContact(
                        customerId,
                        contactId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Customer contact deactivated successfully",
                        response
                )
        );
    }
}
