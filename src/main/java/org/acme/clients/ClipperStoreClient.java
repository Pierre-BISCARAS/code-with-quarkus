package org.acme.clients;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.models.dto.clipper.ClipperPurchaseRequest;
import org.acme.models.dto.clipper.ClipperStoreResponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "clipper-api")
@Path("/api/stores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ClipperStoreClient {

    @GET
    List<ClipperStoreResponse> listStores();

    @GET
    @Path("/{id}")
    ClipperStoreResponse getStore(@PathParam("id") Long id);

    @POST
    @Path("/{id}/purchase")
    ClipperStoreResponse purchase(@PathParam("id") Long id, ClipperPurchaseRequest request);
}
