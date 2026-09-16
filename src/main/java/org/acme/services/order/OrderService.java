package org.acme.services.order;

import org.acme.models.dao.Order;
import org.acme.models.dto.OrderRequest;

import java.util.List;

public interface OrderService {
    Order placeOrder(String customerEmail, OrderRequest request);
    List<Order> listOrders(String customerEmail);
    Order getOrder(String customerEmail, Long orderId);
}
