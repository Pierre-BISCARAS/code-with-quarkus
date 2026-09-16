package org.acme.models.dto;

import org.acme.models.dao.DeliveryType;
import org.acme.models.dao.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
    Long id,
    Long storeId,
    Integer quantity,
    DeliveryType deliveryType,
    BigDecimal price,
    Double distanceKm,
    OrderStatus status,
    LocalDateTime orderedAt,
    LocalDateTime estimatedArrivalAt
) {}
