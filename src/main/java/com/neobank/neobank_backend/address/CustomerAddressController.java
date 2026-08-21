package com.neobank.neobank_backend.address;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/addresses")
@RequiredArgsConstructor
public class CustomerAddressController {

    private final CustomerAddressService customerAddressService;


    /**
     * Add a new address for a customer.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CustomerAddressResponse> addAddress(
            @PathVariable Long customerId,
            @Valid @RequestBody AddCustomerAddressRequest request
    ) {

        CustomerAddressResponse response =
                customerAddressService.addAddress(
                        customerId,
                        request
                );

        return ApiResponse.success(
                "Customer address created successfully",
                response
        );
    }


    /**
     * Get all addresses of a customer.
     */
    @GetMapping
    public ApiResponse<List<CustomerAddressResponse>> getCustomerAddresses(
            @PathVariable Long customerId
    ) {

        List<CustomerAddressResponse> response =
                customerAddressService.getCustomerAddresses(customerId);

        return ApiResponse.success(
                "Customer addresses retrieved successfully",
                response
        );
    }


    /**
     * Get a specific customer address.
     */
    @GetMapping("/{addressId}")
    public ApiResponse<CustomerAddressResponse> getAddressById(
            @PathVariable Long customerId,
            @PathVariable Long addressId
    ) {

        CustomerAddressResponse response =
                customerAddressService.getAddressById(
                        customerId,
                        addressId
                );

        return ApiResponse.success(
                "Customer address retrieved successfully",
                response
        );
    }


    /**
     * Update a customer address.
     */
    @PutMapping("/{addressId}")
    public ApiResponse<CustomerAddressResponse> updateAddress(
            @PathVariable Long customerId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddCustomerAddressRequest request
    ) {

        CustomerAddressResponse response =
                customerAddressService.updateAddress(
                        customerId,
                        addressId,
                        request
                );

        return ApiResponse.success(
                "Customer address updated successfully",
                response
        );
    }


    /**
     * Deactivate a customer address.
     */
    @PatchMapping("/{addressId}/deactivate")
    public ApiResponse<CustomerAddressResponse> deactivateAddress(
            @PathVariable Long customerId,
            @PathVariable Long addressId
    ) {

        CustomerAddressResponse response =
                customerAddressService.deactivateAddress(
                        customerId,
                        addressId
                );

        return ApiResponse.success(
                "Customer address deactivated successfully",
                response
        );
    }
}