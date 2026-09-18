package org.acme.services.customer;

import io.quarkus.elytron.security.common.BcryptUtil;
import org.acme.models.dao.Customer;
import org.acme.models.dto.CustomerRequest;
import org.acme.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImpTest {

    @Mock
    CustomerRepository repository;

    @InjectMocks
    CustomerServiceImp service;

    @Test
    void register_shouldHashPasswordBeforePersisting() {
        CustomerRequest request = new CustomerRequest("alice@test.com", "s3cret!", "1 rue de Paris");

        Customer result = service.register(request);

        assertNotEquals("s3cret!", result.password);
        assertTrue(BcryptUtil.matches("s3cret!", result.password));
        verify(repository).persist(result);
    }

    @Test
    void register_shouldDefaultRoleToUser() {
        CustomerRequest request = new CustomerRequest("bob@test.com", "pw", "Amiens");

        Customer result = service.register(request);

        assertEquals("user", result.role);
    }

    @Test
    void register_shouldCopyEmailAndAddressFromRequest() {
        CustomerRequest request = new CustomerRequest("carla@test.com", "pw", "9 rue de Nice");

        Customer result = service.register(request);

        assertEquals("carla@test.com", result.email);
        assertEquals("9 rue de Nice", result.address);
    }

    @Test
    void getByEmail_shouldDelegateToRepository() {
        Customer existing = new Customer();
        existing.email = "dora@test.com";
        when(repository.findByEmail("dora@test.com")).thenReturn(existing);

        Customer result = service.getByEmail("dora@test.com");

        assertSame(existing, result);
    }

    @Test
    void getByEmail_unknownEmail_shouldReturnNull() {
        when(repository.findByEmail("ghost@test.com")).thenReturn(null);

        assertNull(service.getByEmail("ghost@test.com"));
    }
}
