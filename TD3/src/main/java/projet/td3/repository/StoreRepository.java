package projet.td3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projet.td3.model.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
}
