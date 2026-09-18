package org.acme.services.order;

import org.acme.models.dao.Customer;
import org.acme.models.dao.Order;
import org.acme.models.dao.OrderStatus;
import org.acme.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliverySchedulerTest {

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    DeliveryScheduler scheduler;

    private Order dueOrder(Customer customer, int quantity) {
        Order order = new Order();
        order.customer = customer;
        order.quantity = quantity;
        order.status = OrderStatus.IN_TRANSIT;
        return order;
    }

    @Test
    void deliverOrders_shouldMarkDeliveredAndIncrementCustomerStock() {
        Customer customer = new Customer();
        customer.paperclipStock = 5;
        Order order = dueOrder(customer, 3);

        when(orderRepository.listDueForDelivery(any())).thenReturn(List.of(order));

        scheduler.deliverOrders();

        assertEquals(OrderStatus.DELIVERED, order.status);
        assertEquals(8, customer.paperclipStock);
    }

    @Test
    void deliverOrders_multipleOrdersSameCustomer_shouldAccumulateStock() {
        Customer customer = new Customer();
        customer.paperclipStock = 0;
        Order first = dueOrder(customer, 2);
        Order second = dueOrder(customer, 5);

        when(orderRepository.listDueForDelivery(any())).thenReturn(List.of(first, second));

        scheduler.deliverOrders();

        assertEquals(7, customer.paperclipStock);
        assertEquals(OrderStatus.DELIVERED, first.status);
        assertEquals(OrderStatus.DELIVERED, second.status);
    }

    @Test
    void deliverOrders_noneDue_shouldDoNothing() {
        when(orderRepository.listDueForDelivery(any())).thenReturn(List.of());

        scheduler.deliverOrders();
    }
}
