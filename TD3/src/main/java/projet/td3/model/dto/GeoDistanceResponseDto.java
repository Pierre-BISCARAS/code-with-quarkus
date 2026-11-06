package projet.td3.model.dto;

public record GeoDistanceResponseDto(GeoPointDto from, GeoPointDto to, double distanceKm) {
}
