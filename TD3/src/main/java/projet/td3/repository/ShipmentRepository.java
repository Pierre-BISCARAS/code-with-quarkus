package projet.td3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projet.td3.model.entity.Shipment;
import projet.td3.model.entity.ShipmentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    List<Shipment> findByStoreIdAndStatus(Long storeId, ShipmentStatus status);

    List<Shipment> findByStatusAndArrivalTimeLessThanEqual(ShipmentStatus status, LocalDateTime time);
}
