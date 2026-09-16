package org.acme.models.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
    @NotBlank(message = "email can not be empty") String email,
    @NotBlank(message = "password can not be empty") String password,
    String address
) {}