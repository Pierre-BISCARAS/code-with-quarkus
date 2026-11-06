package projet.td3.geo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeoFeature(GeoGeometry geometry, GeoProperties properties) {
}
