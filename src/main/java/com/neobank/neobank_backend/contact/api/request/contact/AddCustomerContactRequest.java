package com.neobank.neobank_backend.contact.api.request.contact;


import com.neobank.neobank_backend.contact.domain.contact.ContactType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddCustomerContactRequest {

    @NotNull(message = "Contact type is required")
    private ContactType contactType;

    @NotBlank(message = "Contact value is required")
    private String contactValue;

    private boolean primary;
}