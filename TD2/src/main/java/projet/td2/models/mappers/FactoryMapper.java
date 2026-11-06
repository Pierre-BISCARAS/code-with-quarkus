package projet.td2.models.mappers;


import org.mapstruct.Mapper;
import projet.td2.models.dao.Factory;
import projet.td2.models.dto.FactoryDto;

@Mapper(componentModel = "spring")
public interface FactoryMapper {
    Factory toEntity(FactoryDto request);
}