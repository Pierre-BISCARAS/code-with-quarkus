package projet.td3.geo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import projet.td3.exception.GeocodingException;
import projet.td3.geo.dto.GeoFeature;

@Service
@RequiredArgsConstructor
public class GeoService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private final GeoClient geoClient;

    public GeoCoordinates geocode(String address) {
        GeoFeature feature = geoClient.search(address)
                .orElseThrow(() -> new GeocodingException("Address not found: " + address));

        // GeoJSON coordinates are ordered [longitude, latitude]
        double longitude = feature.geometry().coordinates().get(0);
        double latitude = feature.geometry().coordinates().get(1);
        String label = feature.properties() != null ? feature.properties().label() : address;

        return new GeoCoordinates(latitude, longitude, label);
    }

    public String reverseGeocode(double latitude, double longitude) {
        return geoClient.reverse(latitude, longitude)
                .map(GeoFeature::properties)
                .map(projet.td3.geo.dto.GeoProperties::label)
                .orElse(null);
    }

    /**
     * Distance à vol d'oiseau entre deux points GPS (formule de Haversine).
     */
    public double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
