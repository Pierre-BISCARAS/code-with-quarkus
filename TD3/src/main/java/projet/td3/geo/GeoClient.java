package projet.td3.geo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import projet.td3.geo.dto.GeoFeature;
import projet.td3.geo.dto.GeoFeatureCollection;

import java.util.Optional;

@Component
public class GeoClient {

    private final RestClient restClient;

    public GeoClient(RestClient.Builder builder, @Value("${geo.api.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public Optional<GeoFeature> search(String address) {
        GeoFeatureCollection response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/")
                        .queryParam("q", address)
                        .queryParam("limit", 1)
                        .build())
                .retrieve()
                .body(GeoFeatureCollection.class);

        return firstFeature(response);
    }

    public Optional<GeoFeature> reverse(double latitude, double longitude) {
        GeoFeatureCollection response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/reverse/")
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .queryParam("limit", 1)
                        .build())
                .retrieve()
                .body(GeoFeatureCollection.class);

        return firstFeature(response);
    }

    private Optional<GeoFeature> firstFeature(GeoFeatureCollection response) {
        if (response == null || response.features() == null || response.features().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(response.features().getFirst());
    }
}
