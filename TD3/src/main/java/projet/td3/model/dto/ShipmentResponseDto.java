package projet.td3.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import projet.td3.model.entity.ShipmentStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentResponseDto {

    private Long id;

    private Long factoryId;
    private String factoryName;
    private Double factoryLatitude;
    private Double factoryLongitude;

    private Long storeId;
    private String storeName;
    private Double storeLatitude;
    private Double storeLongitude;

    private Integer quantity;
    private Double distanceKm;
    private ShipmentStatus status;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
}
