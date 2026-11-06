package projet.td3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import projet.td3.geo.GeoService;
import projet.td3.model.dto.FactoryWithDistanceDto;
import projet.td3.model.dto.ShipmentResponseDto;
import projet.td3.model.dto.SupplyRequestDto;
import projet.td3.model.entity.Factory;
import projet.td3.model.entity.Shipment;
import projet.td3.model.entity.ShipmentStatus;
import projet.td3.model.entity.Store;
import projet.td3.repository.ShipmentRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private static final double SOUND_SPEED_KMH = 1235.0;

    private final ShipmentRepository shipmentRepository;
    private final FactoryService factoryService;
    private final StoreService storeService;
    private final GeoService geoService;

    public List<FactoryWithDistanceDto> listFactoriesForStore(Long storeId) {
        Store store = storeService.findEntity(storeId);

        return factoryService.getAllEntities().stream()
                .map(factory -> {
                    double distance = geoService.haversineKm(
                            factory.getLatitude(), factory.getLongitude(),
                            store.getLatitude(), store.getLongitude());
                    return factoryService.toWithDistanceDto(factory, distance);
                })
                .toList();
    }

    public ShipmentResponseDto supply(Long storeId, SupplyRequestDto request) {
        Store store = storeService.findEntity(storeId);
        Factory factory = factoryService.findEntity(request.getFactoryId());

        double distanceKm = geoService.haversineKm(
                factory.getLatitude(), factory.getLongitude(),
                store.getLatitude(), store.getLongitude());

        // Décrémentation immédiate du stock de l'usine (retry-safe en cas de conflit
        // avec la production automatique). Lève InsufficientStockException si besoin.
        factoryService.removeStockWithRetry(factory.getId(), request.getQuantity());

        long travelSeconds = Math.round((distanceKm / SOUND_SPEED_KMH) * 3600);
        LocalDateTime departureTime = LocalDateTime.now();
        LocalDateTime arrivalTime = departureTime.plusSeconds(travelSeconds);

        Shipment shipment = Shipment.builder()
                .factory(factory)
                .store(store)
                .quantity(request.getQuantity())
                .distanceKm(distanceKm)
                .status(ShipmentStatus.IN_TRANSIT)
                .departureTime(departureTime)
                .arrivalTime(arrivalTime)
                .build();

        return toResponseDto(shipmentRepository.save(shipment));
    }

    public List<ShipmentResponseDto> listShipments(Long storeId, boolean delivered) {
        storeService.findEntity(storeId);
        ShipmentStatus status = delivered ? ShipmentStatus.DELIVERED : ShipmentStatus.IN_TRANSIT;

        return shipmentRepository.findByStoreIdAndStatus(storeId, status).stream()
                .map(this::toResponseDto)
                .toList();
    }

    /**
     * Traite les expéditions arrivées à destination : crédite le stock du magasin
     * (retry-safe) puis marque l'expédition comme livrée.
     */
    public void processDueShipments() {
        List<Shipment> due = shipmentRepository.findByStatusAndArrivalTimeLessThanEqual(
                ShipmentStatus.IN_TRANSIT, LocalDateTime.now());

        for (Shipment shipment : due) {
            storeService.addStockWithRetry(shipment.getStore().getId(), shipment.getQuantity());
            shipment.setStatus(ShipmentStatus.DELIVERED);
            shipmentRepository.save(shipment);
        }
    }

    private ShipmentResponseDto toResponseDto(Shipment shipment) {
        Factory factory = shipment.getFactory();
        Store store = shipment.getStore();

        return ShipmentResponseDto.builder()
                .id(shipment.getId())
                .factoryId(factory.getId())
                .factoryName(factory.getName())
                .factoryLatitude(factory.getLatitude())
                .factoryLongitude(factory.getLongitude())
                .storeId(store.getId())
                .storeName(store.getName())
                .storeLatitude(store.getLatitude())
                .storeLongitude(store.getLongitude())
                .quantity(shipment.getQuantity())
                .distanceKm(shipment.getDistanceKm())
                .status(shipment.getStatus())
                .departureTime(shipment.getDepartureTime())
                .arrivalTime(shipment.getArrivalTime())
                .build();
    }
}
