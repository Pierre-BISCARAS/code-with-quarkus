package org.acme.services.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import org.acme.clients.ClipperGeoClient;
import org.acme.clients.ClipperStoreClient;
import org.acme.models.dao.Customer;
import org.acme.models.dao.DeliveryType;
import org.acme.models.dao.Order;
import org.acme.models.dao.OrderStatus;
import org.acme.models.dto.OrderRequest;
import org.acme.models.dto.clipper.ClipperDistanceResponse;
import org.acme.models.dto.clipper.ClipperPurchaseRequest;
import org.acme.models.dto.clipper.ClipperStoreResponse;
import org.acme.repositories.CustomerRepository;
import org.acme.repositories.OrderRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class OrderServiceImp implements OrderService {

    private static final BigDecimal PRICE_PER_PAPERCLIP = new BigDecimal("0.10");

    @Inject
    CustomerRepository customerRepository;

    @Inject
    OrderRepository orderRepository;

    @Inject
    @RestClient
    ClipperStoreClient storeClient;

    @Inject
    @RestClient
    ClipperGeoClient geoClient;

    @Override
    @Transactional
    public Order placeOrder(String customerEmail, OrderRequest request) {
        if (request.quantity() == null || request.quantity() <= 0) {
            throw new BadRequestException("quantity must be positive");
        }

        Customer customer = customerRepository.findByEmail(customerEmail);

        ClipperStoreResponse store;
        ClipperDistanceResponse distance;
        try {
            store = storeClient.getStore(request.storeId());
            storeClient.purchase(request.storeId(), new ClipperPurchaseRequest(request.quantity()));
            distance = geoClient.getDistance(store.address(), customer.address);
        } catch (WebApplicationException e) {
            throw new WebApplicationException("Clipper rejected the order: " + e.getMessage(), e.getResponse().getStatus());
        }

        DeliveryType deliveryType = request.deliveryType();
        double deliverySeconds = (distance.distanceKm() / deliveryType.speedKmh) * 3600;

        BigDecimal price = PRICE_PER_PAPERCLIP
                .multiply(BigDecimal.valueOf(request.quantity()))
                .add(deliveryType.cost);

        Order order = new Order();
        order.customer = customer;
        order.storeId = request.storeId();
        order.quantity = request.quantity();
        order.deliveryType = deliveryType;
        order.price = price;
        order.distanceKm = distance.distanceKm();
        order.status = OrderStatus.IN_TRANSIT;
        order.orderedAt = LocalDateTime.now();
        order.estimatedArrivalAt = order.orderedAt.plusSeconds((long) deliverySeconds);

        orderRepository.persist(order);
        return order;
    }

    @Override
    public List<Order> listOrders(String customerEmail) {
        Customer customer = customerRepository.findByEmail(customerEmail);
        return orderRepository.listByCustomer(customer);
    }

    @Override
    public Order getOrder(String customerEmail, Long orderId) {
        Customer customer = customerRepository.findByEmail(customerEmail);
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new NotFoundException("Order " + orderId + " not found");
        }
        if (!order.customer.id.equals(customer.id)) {
            throw new ForbiddenException("You can only access your own orders");
        }
        return order;
    }
}
