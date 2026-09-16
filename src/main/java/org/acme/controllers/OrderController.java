package org.acme.controllers;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.acme.models.dto.OrderRequest;
import org.acme.models.dto.OrderResponse;
import org.acme.models.mappers.OrderMapper;
import org.acme.services.order.OrderService;

import java.util.List;

@Path("/api/orders")
public class OrderController {

    @Inject
    OrderService service;

    @Inject
    OrderMapper mapper;

    @Context
    SecurityContext securityContext;

    @POST
    @RolesAllowed("user")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response placeOrder(@Valid OrderRequest request) {
        String email = securityContext.getUserPrincipal().getName();
        OrderResponse response = mapper.toResponse(service.placeOrder(email, request));
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @RolesAllowed("user")
    public List<OrderResponse> listOrders() {
        String email = securityContext.getUserPrincipal().getName();
        return service.listOrders(email).stream().map(mapper::toResponse).toList();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("user")
    public OrderResponse getOrder(@PathParam("id") Long id) {
        String email = securityContext.getUserPrincipal().getName();
        return mapper.toResponse(service.getOrder(email, id));
    }
}
