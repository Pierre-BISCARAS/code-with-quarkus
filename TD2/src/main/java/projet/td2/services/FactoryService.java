package projet.td2.services;

import projet.td2.configuration.exception.types.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import projet.td2.models.dao.Factory;
import projet.td2.repositories.FactoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FactoryService {

    private final FactoryRepository repository;

    public Factory saveAccount(Factory request) {
        return repository.save(request);
    }


    public Factory getFactoryById(Integer id){
        return repository.findById(id).orElseThrow(()-> new NotFoundException("La factory n'existe pas"));
    }

    public List<Factory> getAllFactories(){
        return repository.findAll();
    }

    public Factory putFactoryById(Integer id, Factory request) {
        Factory update = getFactoryById(id);

        update.setName(request.getName());
        update.setProduction(request.getProduction());
        update.setStock(request.getStock());
        update.setLatitude(request.getLatitude());
        update.setLongitude(request.getLongitude());

        return repository.save(update);
    }


    public Factory PatchFactoryById(Integer id, Factory request) {

        Factory update = getFactoryById(id);

        if (request.getName() != null) update.setName(request.getName());
        if (request.getProduction() != null) update.setProduction(request.getProduction());
        if (request.getStock() != null) update.setStock(request.getStock());
        if (request.getLatitude() != null) update.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) update.setLongitude(request.getLongitude());

        return repository.save(update);

    }

    public Factory PostProductionById(Integer id, Integer quantity) {
        Factory production = getFactoryById(id);

        production.addProduction(quantity);

        return repository.save(production);
    }

    public void deleteFactoryById(Integer id) {
        Factory factory = getFactoryById(id);

        repository.deleteById(id);
    }

}