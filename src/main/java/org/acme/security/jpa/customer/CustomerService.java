package org.acme.security.jpa.customer;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/costumers")
public class CustomerService {

    @GET
    @RolesAllowed("user")
    @Path("/me")
    public String me(@Context SecurityContext securityContext) {
        return securityContext.getUserPrincipal().getName();
    }

    @GET
    @Path("/{id}")
    public CustomerResponse getCustomer(@PathParam("id") Long id) {
        String email = securityContext.getUserPrincipal().getName();
        Customer customer = Customer.findByEmail(email);
        if (customer == null || !customer.id.equals(id)) {
            throw new ForbiddenException("You can only access your own profile");
        }
        return authService.getProfile(email);
    }
}