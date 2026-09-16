package org.acme.models.dto.clipper;

public record ClipperStoreResponse(
    Long id,
    String name,
    Integer stock,
    String address,
    Double latitude,
    Double longitude
) {}
