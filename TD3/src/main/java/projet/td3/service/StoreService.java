package projet.td3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import projet.td3.exception.ConcurrentUpdateException;
import projet.td3.exception.NotFoundException;
import projet.td3.geo.GeoCoordinates;
import projet.td3.geo.GeoService;
import projet.td3.model.dto.StoreCreateDto;
import projet.td3.model.dto.StoreResponseDto;
import projet.td3.model.dto.StoreUpdateDto;
import projet.td3.model.entity.Store;
import projet.td3.model.mapper.StoreMapper;
import projet.td3.repository.StoreRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private static final int MAX_RETRIES = 3;

    private final StoreRepository repository;
    private final StoreMapper mapper;
    private final GeoService geoService;
    private final TransactionTemplate retryTransactionTemplate;

    public StoreResponseDto create(StoreCreateDto dto) {
        GeoCoordinates coords = geoService.geocode(dto.getAddress());

        Store store = Store.builder()
                .name(dto.getName())
                .stock(0)
                .latitude(coords.latitude())
                .longitude(coords.longitude())
                .build();

        return toResponseDto(repository.save(store));
    }

    public List<StoreResponseDto> getAll() {
        return repository.findAll().stream().map(this::toResponseDto).toList();
    }

    public StoreResponseDto getById(Long id) {
        return toResponseDto(findEntity(id));
    }

    public StoreResponseDto replace(Long id, StoreCreateDto dto) {
        Store store = findEntity(id);
        GeoCoordinates coords = geoService.geocode(dto.getAddress());

        store.setName(dto.getName());
        store.setLatitude(coords.latitude());
        store.setLongitude(coords.longitude());

        return toResponseDto(repository.save(store));
    }

    public StoreResponseDto update(Long id, StoreUpdateDto dto) {
        Store store = findEntity(id);

        if (dto.getName() != null) store.setName(dto.getName());
        if (dto.getStock() != null) store.setStock(dto.getStock());
        if (dto.getAddress() != null) {
            GeoCoordinates coords = geoService.geocode(dto.getAddress());
            store.setLatitude(coords.latitude());
            store.setLongitude(coords.longitude());
        }

        return toResponseDto(repository.save(store));
    }

    public void delete(Long id) {
        Store store = findEntity(id);
        repository.delete(store);
    }

    Store findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Store " + id + " not found"));
    }

    /**
     * Augmente le stock du magasin (livraison) de manière sûre en cas d'écritures
     * concurrentes (plusieurs expéditions livrées en même temps) : retry sur conflit de version.
     */
    public Store addStockWithRetry(Long id, int delta) {
        ObjectOptimisticLockingFailureException lastError = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return retryTransactionTemplate.execute(status -> {
                    Store store = findEntity(id);
                    store.setStock(store.getStock() + delta);
                    return repository.save(store);
                });
            } catch (ObjectOptimisticLockingFailureException e) {
                lastError = e;
            }
        }

        throw new ConcurrentUpdateException(
                "Failed to update store stock after " + MAX_RETRIES + " attempts due to concurrent updates",
                lastError);
    }

    private StoreResponseDto toResponseDto(Store store) {
        StoreResponseDto dto = mapper.toResponseDto(store);
        dto.setAddress(geoService.reverseGeocode(store.getLatitude(), store.getLongitude()));
        return dto;
    }
}
