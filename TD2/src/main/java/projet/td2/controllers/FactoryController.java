package projet.td2.controllers;


import projet.td2.models.dao.Factory;
import projet.td2.models.dto.FactoryDto;
import projet.td2.models.mappers.FactoryMapper;
import projet.td2.services.FactoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FactoryController {
    private final FactoryMapper mapper;
    private final FactoryService service;

    @PostMapping("/factories")
    public ResponseEntity<Factory> saveAccount(@RequestBody @Valid FactoryDto request){
        return ResponseEntity.ok(service.saveAccount(mapper.toEntity(request)));
    }


    @GetMapping("/factories")
    public ResponseEntity<List<Factory>> getAllFactories(){
        return ResponseEntity.ok(service.getAllFactories());
    }

    @GetMapping("/factories/{id}")
    public ResponseEntity<Factory> getFactory(@PathVariable Integer id){
        return ResponseEntity.ok(service.getFactoryById(id));
    }


    @PutMapping("/factories/{id}")
    public ResponseEntity<Factory> putFactoryById(@PathVariable Integer id, @RequestBody @Valid FactoryDto request){
        return  ResponseEntity.ok(service.putFactoryById(id, mapper.toEntity(request)));
    }

    @PatchMapping("/factories/{id}")
    public ResponseEntity<Factory> patchFactoryById(@PathVariable Integer id, @RequestBody @Valid FactoryDto request){
        return ResponseEntity.ok(service.PatchFactoryById(id, mapper.toEntity(request)));
    }

    @PostMapping("/factories/{id}/produce/{quantity}")
    public ResponseEntity<Factory> PostProductionById(@PathVariable Integer id, @PathVariable Integer quantity) {
        return ResponseEntity.ok(service.PostProductionById(id, quantity));
    }

    @DeleteMapping("/factories/{id}")
    public ResponseEntity<Factory> deleteFactoryById(@PathVariable Integer id){
        service.deleteFactoryById(id);
        return ResponseEntity.noContent().build();
    }
}