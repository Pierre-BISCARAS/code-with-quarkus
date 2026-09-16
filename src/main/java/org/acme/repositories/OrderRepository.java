package org.acme.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.models.dao.Customer;
import org.acme.models.dao.Order;
import org.acme.models.dao.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {

    public List<Order> listByCustomer(Customer customer) {
        return list("customer", customer);
    }

    public List<Order> listDueForDelivery(LocalDateTime now) {
        return list("status = ?1 and estimatedArrivalAt <= ?2", OrderStatus.IN_TRANSIT, now);
    }
}
