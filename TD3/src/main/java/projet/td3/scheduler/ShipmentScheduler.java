package projet.td3.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import projet.td3.service.ShipmentService;

@Component
@RequiredArgsConstructor
public class ShipmentScheduler {

    private final ShipmentService shipmentService;

    @Scheduled(fixedRate = 5000)
    public void checkDeliveries() {
        shipmentService.processDueShipments();
    }
}
