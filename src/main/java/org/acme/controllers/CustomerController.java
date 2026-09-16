package org.acme.controllers;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.acme.models.dao.Customer;
import org.acme.models.dto.CustomerRequest;
import org.acme.models.dto.CustomerResponse;
import org.acme.models.mappers.CustomerMapper;
import org.acme.services.customer.CustomerService;

@Path("/api")
public class CustomerController {

    @Inject
    CustomerService service;

    @Inject
    CustomerMapper mapper;

    @Context
    SecurityContext securityContext;

    @POST
    @PermitAll
    @Path("/auth/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(CustomerRequest request) {
        service.register(request);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @RolesAllowed("user")
    @Path("/customers/me")
    public String me() {
        return securityContext.getUserPrincipal().getName();
    }

    @GET
    @RolesAllowed("user")
    @Path("/customers/{id}")
    public CustomerResponse getCustomer(@PathParam("id") Long id) {
        String email = securityContext.getUserPrincipal().getName();
        Customer customer = service.getByEmail(email);
        if (customer == null || !customer.id.equals(id)) {
            throw new ForbiddenException("You can only access your own profile");
        }
        return mapper.toResponse(customer);
    }
}
