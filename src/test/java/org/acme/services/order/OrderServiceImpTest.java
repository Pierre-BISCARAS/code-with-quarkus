package org.acme.services.order;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImpTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    ClipperStoreClient storeClient;

    @Mock
    ClipperGeoClient geoClient;

    @InjectMocks
    OrderServiceImp service;

    private Customer customer(Long id, String email, String address) {
        Customer c = new Customer();
        c.id = id;
        c.email = email;
        c.address = address;
        return c;
    }

    @ParameterizedTest
    @EnumSource(DeliveryType.class)
    void placeOrder_shouldComputePriceFromQuantityAndDeliveryType(DeliveryType deliveryType) {
        Customer customer = customer(1L, "alice@test.com", "Amiens");
        when(customerRepository.findByEmail("alice@test.com")).thenReturn(customer);
        when(storeClient.getStore(2L)).thenReturn(new ClipperStoreResponse(2L, "STORE B", 10, "Paris", 48.0, 2.0));
        when(geoClient.getDistance("Paris", "Amiens")).thenReturn(
                new ClipperDistanceResponse("Paris", 48.0, 2.0, "Amiens", 49.0, 2.3, 100.0));

        OrderRequest request = new OrderRequest(2L, 4, deliveryType);
        Order order = service.placeOrder("alice@test.com", request);

        BigDecimal expectedPrice = new BigDecimal("0.10").multiply(BigDecimal.valueOf(4)).add(deliveryType.cost);
        assertEquals(0, expectedPrice.compareTo(order.price));
        assertEquals(OrderStatus.IN_TRANSIT, order.status);
        verify(storeClient).purchase(2L, new ClipperPurchaseRequest(4));
        verify(orderRepository).persist(order);
    }

    @Test
    void placeOrder_shouldComputeEstimatedArrivalFromDistanceAndSpeed() {
        Customer customer = customer(1L, "bob@test.com", "Lyon");
        when(customerRepository.findByEmail("bob@test.com")).thenReturn(customer);
        when(storeClient.getStore(3L)).thenReturn(new ClipperStoreResponse(3L, "STORE C", 10, "Marseille", 43.0, 5.0));
        when(geoClient.getDistance("Marseille", "Lyon")).thenReturn(
                new ClipperDistanceResponse("Marseille", 43.0, 5.0, "Lyon", 45.0, 4.8, 300.0));

        OrderRequest request = new OrderRequest(3L, 1, DeliveryType.BASIC);
        Order order = service.placeOrder("bob@test.com", request);

        // 300 km a 100 km/h = 3h = 10800s
        assertEquals(order.orderedAt.plusSeconds(10800), order.estimatedArrivalAt);
    }

    @Test
    void placeOrder_negativeOrZeroQuantity_shouldBeRejectedBeforeCallingClipper() {
        OrderRequest request = new OrderRequest(2L, -5, DeliveryType.BASIC);

        assertThrows(BadRequestException.class, () -> service.placeOrder("alice@test.com", request));
        verify(storeClient, never()).getStore(anyLong());
    }

    @Test
    void placeOrder_whenClipperRejectsPurchase_shouldPropagateSameStatusCode() {
        Customer customer = customer(1L, "carla@test.com", "Amiens");
        when(customerRepository.findByEmail("carla@test.com")).thenReturn(customer);
        when(storeClient.getStore(2L)).thenReturn(new ClipperStoreResponse(2L, "STORE B", 1, "Paris", 48.0, 2.0));
        when(storeClient.purchase(anyLong(), any())).thenThrow(new WebApplicationException(409));

        OrderRequest request = new OrderRequest(2L, 50, DeliveryType.BASIC);

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> service.placeOrder("carla@test.com", request));
        assertEquals(409, ex.getResponse().getStatus());
    }

    @Test
    void listOrders_shouldReturnOnlyThatCustomersOrders() {
        Customer customer = customer(1L, "dora@test.com", "Amiens");
        when(customerRepository.findByEmail("dora@test.com")).thenReturn(customer);
        List<Order> orders = List.of(new Order(), new Order());
        when(orderRepository.listByCustomer(customer)).thenReturn(orders);

        assertEquals(orders, service.listOrders("dora@test.com"));
    }

    @Test
    void getOrder_unknownId_shouldThrowNotFoundException() {
        when(customerRepository.findByEmail("eve@test.com")).thenReturn(customer(1L, "eve@test.com", "Amiens"));
        when(orderRepository.findById(999L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> service.getOrder("eve@test.com", 999L));
    }

    @Test
    void getOrder_belongingToAnotherCustomer_shouldThrowForbiddenException() {
        Customer requester = customer(1L, "eve@test.com", "Amiens");
        Customer owner = customer(2L, "finn@test.com", "Paris");
        Order order = new Order();
        order.customer = owner;

        when(customerRepository.findByEmail("eve@test.com")).thenReturn(requester);
        when(orderRepository.findById(5L)).thenReturn(order);

        assertThrows(ForbiddenException.class, () -> service.getOrder("eve@test.com", 5L));
    }

    @Test
    void getOrder_ownOrder_shouldReturnIt() {
        Customer requester = customer(1L, "finn@test.com", "Paris");
        Order order = new Order();
        order.customer = requester;

        when(customerRepository.findByEmail("finn@test.com")).thenReturn(requester);
        when(orderRepository.findById(7L)).thenReturn(order);

        assertSame(order, service.getOrder("finn@test.com", 7L));
    }
}
