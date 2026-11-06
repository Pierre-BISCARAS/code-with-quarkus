package projet.td3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projet.td3.model.entity.Factory;

@Repository
public interface FactoryRepository extends JpaRepository<Factory, Long> {
}
