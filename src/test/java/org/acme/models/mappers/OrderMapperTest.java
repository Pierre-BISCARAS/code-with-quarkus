package org.acme.models.mappers;

import org.acme.models.dao.DeliveryType;
import org.acme.models.dao.Order;
import org.acme.models.dao.OrderStatus;
import org.acme.models.dto.OrderResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderMapperTest {

    private final OrderMapper mapper = new OrderMapperImpl();

    @Test
    void toResponse_shouldCopyAllFields() {
        Order order = new Order();
        order.id = 42L;
        order.storeId = 7L;
        order.quantity = 3;
        order.deliveryType = DeliveryType.EXPRESS;
        order.price = new BigDecimal("15.30");
        order.distanceKm = 12.5;
        order.status = OrderStatus.IN_TRANSIT;
        order.orderedAt = LocalDateTime.of(2026, 1, 1, 10, 0);
        order.estimatedArrivalAt = LocalDateTime.of(2026, 1, 1, 10, 5);

        OrderResponse response = mapper.toResponse(order);

        assertEquals(42L, response.id());
        assertEquals(7L, response.storeId());
        assertEquals(3, response.quantity());
        assertEquals(DeliveryType.EXPRESS, response.deliveryType());
        assertEquals(0, new BigDecimal("15.30").compareTo(response.price()));
        assertEquals(12.5, response.distanceKm());
        assertEquals(OrderStatus.IN_TRANSIT, response.status());
        assertEquals(order.orderedAt, response.orderedAt());
        assertEquals(order.estimatedArrivalAt, response.estimatedArrivalAt());
    }
}
