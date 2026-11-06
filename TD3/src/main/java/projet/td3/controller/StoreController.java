package projet.td3.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projet.td3.model.dto.*;
import projet.td3.service.ShipmentService;
import projet.td3.service.StoreService;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final ShipmentService shipmentService;

    @PostMapping
    public ResponseEntity<StoreResponseDto> create(@RequestBody @Valid StoreCreateDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(storeService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<StoreResponseDto>> getAll() {
        return ResponseEntity.ok(storeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreResponseDto> replace(@PathVariable Long id,
                                                      @RequestBody @Valid StoreCreateDto request) {
        return ResponseEntity.ok(storeService.replace(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StoreResponseDto> update(@PathVariable Long id,
                                                     @RequestBody @Valid StoreUpdateDto request) {
        return ResponseEntity.ok(storeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        storeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/factories")
    public ResponseEntity<List<FactoryWithDistanceDto>> getAvailableFactories(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.listFactoriesForStore(id));
    }

    @PostMapping("/{id}/supply")
    public ResponseEntity<ShipmentResponseDto> supply(@PathVariable Long id,
                                                        @RequestBody @Valid SupplyRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shipmentService.supply(id, request));
    }

    @GetMapping("/{id}/shipments")
    public ResponseEntity<List<ShipmentResponseDto>> getShipments(
            @PathVariable Long id,
            @RequestParam(required = false) String status) {
        boolean delivered = "delivered".equalsIgnoreCase(status);
        return ResponseEntity.ok(shipmentService.listShipments(id, delivered));
    }
}
