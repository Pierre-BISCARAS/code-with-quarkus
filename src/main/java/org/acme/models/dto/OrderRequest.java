package org.acme.models.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.acme.models.dao.DeliveryType;

public record OrderRequest(
    @NotNull(message = "storeId is required") Long storeId,
    @NotNull(message = "quantity is required") @Positive(message = "quantity must be positive") Integer quantity,
    @NotNull(message = "deliveryType is required") DeliveryType deliveryType
) {}
