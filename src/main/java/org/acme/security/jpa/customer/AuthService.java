package org.acme.security.jpa.customer;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.acme.models.dao.Customer;
import org.acme.models.dto.CustomerRequest;
import org.acme.models.dto.CustomerResponse;


@ApplicationScoped
@Path("/api/auth")
public class AuthService {

    @POST
    @PermitAll
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public void register(CustomerRequest request) {
        Customer.add(
                request.email,
                request.password,
                "user",
                request.address
        );
    }

    @GET
    @PermitAll
    public CustomerResponse getProfile(String email) {

    }
}
