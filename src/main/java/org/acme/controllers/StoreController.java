package org.acme.controllers;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.acme.clients.ClipperStoreClient;
import org.acme.models.dto.clipper.ClipperStoreResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@Path("/api/stores")
public class StoreController {

    @Inject
    @RestClient
    ClipperStoreClient client;

    @GET
    @RolesAllowed("user")
    public List<ClipperStoreResponse> listStores() {
        return client.listStores();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("user")
    public ClipperStoreResponse getStore(@PathParam("id") Long id) {
        return client.getStore(id);
    }
}
