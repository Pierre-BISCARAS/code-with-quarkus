package org.acme.models.dto;

import java.math.BigDecimal;

public record CustomerResponse(
    String email,
    String role,
    BigDecimal balance,
    Integer paperclipStock
) {}
