package org.acme.controllers;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.acme.models.dao.Customer;
import org.acme.models.dto.CustomerRequest;

@ApplicationScoped
@Path("/api/auth")
public class CostumerController {

    @POST
    @PermitAll
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public void register(CustomerRequest request) {

        Customer customer = service.register(request);
        return ;
    }
}
