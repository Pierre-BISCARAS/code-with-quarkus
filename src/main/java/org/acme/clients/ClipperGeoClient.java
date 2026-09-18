package org.acme.clients;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.acme.models.dto.clipper.ClipperDistanceResponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "clipper-api")
@ApplicationScoped
@Path("/api/geo")
@Produces(MediaType.APPLICATION_JSON)
public interface ClipperGeoClient {

    @GET
    @Path("/distance")
    ClipperDistanceResponse getDistance(@QueryParam("from") String from, @QueryParam("to") String to);
}
