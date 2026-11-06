package projet.td3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import projet.td3.exception.ConcurrentUpdateException;
import projet.td3.exception.InsufficientStockException;
import projet.td3.exception.NotFoundException;
import projet.td3.geo.GeoCoordinates;
import projet.td3.geo.GeoService;
import projet.td3.model.dto.FactoryCreateDto;
import projet.td3.model.dto.FactoryResponseDto;
import projet.td3.model.dto.FactoryUpdateDto;
import projet.td3.model.dto.FactoryWithDistanceDto;
import projet.td3.model.entity.Factory;
import projet.td3.model.mapper.FactoryMapper;
import projet.td3.repository.FactoryRepository;

import java.util.List;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class FactoryService {

    private static final int MAX_RETRIES = 3;

    private final FactoryRepository repository;
    private final FactoryMapper mapper;
    private final GeoService geoService;
    private final TransactionTemplate retryTransactionTemplate;

    public FactoryResponseDto create(FactoryCreateDto dto) {
        GeoCoordinates coords = geoService.geocode(dto.getAddress());

        Factory factory = Factory.builder()
                .name(dto.getName())
                .production(dto.getProduction())
                .stock(0)
                .latitude(coords.latitude())
                .longitude(coords.longitude())
                .build();

        return toResponseDto(repository.save(factory));
    }

    public List<FactoryResponseDto> getAll() {
        return repository.findAll().stream().map(this::toResponseDto).toList();
    }

    public FactoryResponseDto getById(Long id) {
        return toResponseDto(findEntity(id));
    }

    public FactoryResponseDto replace(Long id, FactoryCreateDto dto) {
        Factory factory = findEntity(id);
        GeoCoordinates coords = geoService.geocode(dto.getAddress());

        factory.setName(dto.getName());
        factory.setProduction(dto.getProduction());
        factory.setLatitude(coords.latitude());
        factory.setLongitude(coords.longitude());

        return toResponseDto(repository.save(factory));
    }

    public FactoryResponseDto update(Long id, FactoryUpdateDto dto) {
        Factory factory = findEntity(id);

        if (dto.getName() != null) factory.setName(dto.getName());
        if (dto.getProduction() != null) factory.setProduction(dto.getProduction());
        if (dto.getStock() != null) factory.setStock(dto.getStock());
        if (dto.getAddress() != null) {
            GeoCoordinates coords = geoService.geocode(dto.getAddress());
            factory.setLatitude(coords.latitude());
            factory.setLongitude(coords.longitude());
        }

        return toResponseDto(repository.save(factory));
    }

    public void delete(Long id) {
        Factory factory = findEntity(id);
        repository.delete(factory);
    }

    public FactoryResponseDto produce(Long id, int quantity) {
        return toResponseDto(addStockWithRetry(id, quantity));
    }

    public FactoryWithDistanceDto toWithDistanceDto(Factory factory, double distanceKm) {
        FactoryWithDistanceDto dto = mapper.toWithDistanceDto(factory);
        dto.setAddress(geoService.reverseGeocode(factory.getLatitude(), factory.getLongitude()));
        dto.setDistanceKm(distanceKm);
        return dto;
    }

    Factory findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Factory " + id + " not found"));
    }

    public List<Factory> getAllEntities() {
        return repository.findAll();
    }

    /**
     * Augmente le stock de manière sûre en cas d'écritures concurrentes (production automatique
     * vs réapprovisionnement) : verrouillage optimiste (@Version) + retry (max 3 tentatives).
     */
    public Factory addStockWithRetry(Long id, int delta) {
        return withOptimisticRetry(() -> {
            Factory factory = findEntity(id);
            factory.setStock(factory.getStock() + delta);
            return repository.save(factory);
        });
    }

    /**
     * Décrémente le stock (réapprovisionnement) de manière sûre, avec vérification du stock
     * disponible et retry en cas de conflit de version.
     */
    public Factory removeStockWithRetry(Long id, int delta) {
        return withOptimisticRetry(() -> {
            Factory factory = findEntity(id);
            if (factory.getStock() < delta) {
                throw new InsufficientStockException(
                        "Factory " + id + " does not have enough stock (available: "
                                + factory.getStock() + ", requested: " + delta + ")");
            }
            factory.setStock(factory.getStock() - delta);
            return repository.save(factory);
        });
    }

    private Factory withOptimisticRetry(Supplier<Factory> action) {
        ObjectOptimisticLockingFailureException lastError = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return retryTransactionTemplate.execute(status -> action.get());
            } catch (ObjectOptimisticLockingFailureException e) {
                lastError = e;
            }
        }

        throw new ConcurrentUpdateException(
                "Failed to update factory stock after " + MAX_RETRIES + " attempts due to concurrent updates",
                lastError);
    }

    private FactoryResponseDto toResponseDto(Factory factory) {
        FactoryResponseDto dto = mapper.toResponseDto(factory);
        dto.setAddress(geoService.reverseGeocode(factory.getLatitude(), factory.getLongitude()));
        return dto;
    }
}
