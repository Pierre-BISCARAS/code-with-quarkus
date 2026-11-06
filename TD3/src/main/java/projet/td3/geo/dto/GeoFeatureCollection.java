package projet.td3.geo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeoFeatureCollection(List<GeoFeature> features) {
}
