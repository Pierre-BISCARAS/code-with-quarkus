package projet.td3.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import projet.td3.model.entity.Factory;
import projet.td3.repository.FactoryRepository;
import projet.td3.service.FactoryService;

@Component
@RequiredArgsConstructor
public class ProductionScheduler {

    private final FactoryRepository factoryRepository;
    private final FactoryService factoryService;

    @Scheduled(fixedRate = 5000)
    public void produceAll() {
        for (Factory factory : factoryRepository.findAll()) {
            factoryService.addStockWithRetry(factory.getId(), factory.getProduction());
        }
    }
}
