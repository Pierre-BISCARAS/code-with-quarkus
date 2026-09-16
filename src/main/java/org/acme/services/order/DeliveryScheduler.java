package org.acme.services.order;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.models.dao.Order;
import org.acme.models.dao.OrderStatus;
import org.acme.repositories.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class DeliveryScheduler {

    @Inject
    OrderRepository orderRepository;

    @Scheduled(every = "5s")
    @Transactional
    public void deliverOrders() {
        List<Order> dueOrders = orderRepository.listDueForDelivery(LocalDateTime.now());

        for (Order order : dueOrders) {
            order.status = OrderStatus.DELIVERED;
            order.customer.paperclipStock += order.quantity;

            Log.infof("Order #%d delivered: customer '%s' +%d paperclips (stock: %d)",
                    order.id, order.customer.email, order.quantity, order.customer.paperclipStock);
        }
    }
}
