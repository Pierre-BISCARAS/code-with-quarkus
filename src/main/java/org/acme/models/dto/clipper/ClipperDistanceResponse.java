package org.acme.models.dto.clipper;

public record ClipperDistanceResponse(
    String fromAddress,
    Double fromLatitude,
    Double fromLongitude,
    String toAddress,
    Double toLatitude,
    Double toLongitude,
    Double distanceKm
) {}
