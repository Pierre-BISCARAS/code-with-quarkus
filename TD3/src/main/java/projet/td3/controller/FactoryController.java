package projet.td3.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import projet.td3.model.dto.FactoryCreateDto;
import projet.td3.model.dto.FactoryResponseDto;
import projet.td3.model.dto.FactoryUpdateDto;
import projet.td3.service.FactoryService;

import java.util.List;

@RestController
@RequestMapping("/api/factories")
@RequiredArgsConstructor
@Validated
public class FactoryController {

    private final FactoryService service;

    @PostMapping
    public ResponseEntity<FactoryResponseDto> create(@RequestBody @Valid FactoryCreateDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<FactoryResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FactoryResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FactoryResponseDto> replace(@PathVariable Long id,
                                                        @RequestBody @Valid FactoryCreateDto request) {
        return ResponseEntity.ok(service.replace(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FactoryResponseDto> update(@PathVariable Long id,
                                                       @RequestBody @Valid FactoryUpdateDto request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PostMapping("/{id}/produce")
    public ResponseEntity<FactoryResponseDto> produce(@PathVariable Long id,
                                                        @RequestParam @Positive int quantity) {
        return ResponseEntity.ok(service.produce(id, quantity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
