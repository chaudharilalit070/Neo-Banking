package com.neobank.neobank_backend.address.api.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddCustomerAddressRequest {

    @NotNull(message = "Address type is required")
    private AddressType addressType;


    @NotBlank(message = "Address line 1 is required")
    @Size(
            max = 255,
            message = "Address line 1 must not exceed 255 characters"
    )
    private String addressLine1;


    @Size(
            max = 255,
            message = "Address line 2 must not exceed 255 characters"
    )
    private String addressLine2;


    @Size(
            max = 255,
            message = "Landmark must not exceed 255 characters"
    )
    private String landmark;


    @NotBlank(message = "City is required")
    @Size(
            max = 100,
            message = "City must not exceed 100 characters"
    )
    private String city;


    @Size(
            max = 100,
            message = "District must not exceed 100 characters"
    )
    private String district;


    @NotBlank(message = "State is required")
    @Size(
            max = 100,
            message = "State must not exceed 100 characters"
    )
    private String state;


    @NotBlank(message = "Country is required")
    @Size(
            max = 100,
            message = "Country must not exceed 100 characters"
    )
    private String country;


    @NotBlank(message = "Postal code is required")
    @Size(
            max = 20,
            message = "Postal code must not exceed 20 characters"
    )
    private String postalCode;
}