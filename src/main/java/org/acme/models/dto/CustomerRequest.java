package org.acme.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
    @NotBlank(message = "email can not be empty")
    @Email(message = "email must be a valid address")
    @Size(max = 255, message = "email must be at most 255 characters")
    String email,

    @NotBlank(message = "password can not be empty")
    @Size(max = 100, message = "password must be at most 100 characters")
    String password,

    @Size(max = 255, message = "address must be at most 255 characters")
    String address
) {}
