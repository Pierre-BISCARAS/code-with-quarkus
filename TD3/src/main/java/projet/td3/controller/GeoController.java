package projet.td3.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import projet.td3.geo.GeoCoordinates;
import projet.td3.geo.GeoService;
import projet.td3.model.dto.GeoDistanceResponseDto;
import projet.td3.model.dto.GeoPointDto;

@RestController
@RequestMapping("/api/geo")
@RequiredArgsConstructor
public class GeoController {

    private final GeoService geoService;

    @GetMapping("/distance")
    public ResponseEntity<GeoDistanceResponseDto> distance(@RequestParam String from, @RequestParam String to) {
        GeoCoordinates fromCoords = geoService.geocode(from);
        GeoCoordinates toCoords = geoService.geocode(to);

        double distanceKm = geoService.haversineKm(
                fromCoords.latitude(), fromCoords.longitude(),
                toCoords.latitude(), toCoords.longitude());

        GeoDistanceResponseDto response = new GeoDistanceResponseDto(
                new GeoPointDto(fromCoords.label(), fromCoords.latitude(), fromCoords.longitude()),
                new GeoPointDto(toCoords.label(), toCoords.latitude(), toCoords.longitude()),
                distanceKm
        );

        return ResponseEntity.ok(response);
    }
}
