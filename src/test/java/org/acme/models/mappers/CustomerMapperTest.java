package org.acme.models.mappers;

import org.acme.models.dao.Customer;
import org.acme.models.dto.CustomerResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class CustomerMapperTest {

    private final CustomerMapper mapper = new CustomerMapperImpl();

    @Test
    void toResponse_shouldCopyAllFields() {
        Customer customer = new Customer();
        customer.email = "alice@test.com";
        customer.password = "super-secret-hash";
        customer.role = "user";
        customer.balance = BigDecimal.valueOf(500);
        customer.paperclipStock = 3;

        CustomerResponse response = mapper.toResponse(customer);

        assertEquals("alice@test.com", response.email());
        assertEquals("user", response.role());
        assertEquals(0, BigDecimal.valueOf(500).compareTo(response.balance()));
        assertEquals(3, response.paperclipStock());
    }

    @Test
    void toResponse_null_shouldReturnNull() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    void response_shouldNeverExposeAPasswordField() {
        boolean hasPasswordComponent = Arrays.stream(CustomerResponse.class.getRecordComponents())
                .anyMatch(c -> c.getName().toLowerCase().contains("password"));

        assertFalse(hasPasswordComponent, "CustomerResponse must never expose the password field");
    }
}
