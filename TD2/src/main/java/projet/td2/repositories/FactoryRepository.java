package projet.td2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projet.td2.models.dao.Factory;

@Repository
public interface FactoryRepository extends JpaRepository<Factory, Integer> {
}