package org.acme.services.order;

import jakarta.ws.rs.BadRequestException;
import org.acme.clients.ClipperGeoClient;
import org.acme.clients.ClipperStoreClient;
import org.acme.models.dao.Customer;
import org.acme.models.dao.DeliveryType;
import org.acme.models.dao.Order;
import org.acme.models.dto.OrderRequest;
import org.acme.models.dto.clipper.ClipperDistanceResponse;
import org.acme.models.dto.clipper.ClipperStoreResponse;
import org.acme.repositories.CustomerRepository;
import org.acme.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

/**
 * Fuzzing "maison" : au lieu d'un jeu de tests figes, on genere des centaines
 * d'entrees aleatoires (y compris hors des limites autorisees par les
 * annotations de validation) et on verifie que le service ne plante jamais
 * de facon inattendue et respecte toujours ses invariants metier.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceImpFuzzTest {

    private static final int ITERATIONS = 500;
    private static final String CUSTOMER_EMAIL = "fuzz@test.com";
    private static final long STORE_ID = 1L;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    ClipperStoreClient storeClient;

    @Mock
    ClipperGeoClient geoClient;

    OrderServiceImp service;
    Random random;

    @BeforeEach
    void setUp() {
        service = new OrderServiceImp();
        service.customerRepository = customerRepository;
        service.orderRepository = orderRepository;
        service.storeClient = storeClient;
        service.geoClient = geoClient;

        // graine fixe : si le fuzzing trouve un cas cassant, il est reproductible
        random = new Random(42);

        Customer customer = new Customer();
        customer.id = 1L;
        customer.email = CUSTOMER_EMAIL;
        customer.address = "Amiens";
        lenient().when(customerRepository.findByEmail(CUSTOMER_EMAIL)).thenReturn(customer);
        lenient().when(storeClient.getStore(STORE_ID))
                .thenReturn(new ClipperStoreResponse(STORE_ID, "STORE", 1_000_000, "Paris", 48.0, 2.0));
        lenient().when(geoClient.getDistance("Paris", "Amiens"))
                .thenReturn(new ClipperDistanceResponse("Paris", 48.0, 2.0, "Amiens", 49.0, 2.3, 130.0));
    }

    @RepeatedTest(ITERATIONS)
    void placeOrder_randomQuantityAcrossFullIntRange_shouldNeverCrashOrProduceInvalidOrder() {
        int quantity = random.nextInt(); // toute la plage int : negatif, zero, enorme
        DeliveryType deliveryType = DeliveryType.values()[random.nextInt(DeliveryType.values().length)];
        OrderRequest request = new OrderRequest(STORE_ID, quantity, deliveryType);

        if (quantity <= 0) {
            assertThrows(BadRequestException.class,
                    () -> service.placeOrder(CUSTOMER_EMAIL, request),
                    () -> "quantity=" + quantity + " should be rejected cleanly, not silently accepted");
        } else {
            Order order = assertDoesNotThrow(
                    () -> service.placeOrder(CUSTOMER_EMAIL, request),
                    () -> "quantity=" + quantity + " (" + deliveryType + ") crashed unexpectedly");
            assertTrue(order.price.compareTo(BigDecimal.ZERO) > 0,
                    "price should always be strictly positive for quantity=" + quantity);
        }
    }
}
