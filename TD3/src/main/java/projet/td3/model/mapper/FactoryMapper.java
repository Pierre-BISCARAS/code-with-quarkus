package projet.td3.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import projet.td3.model.dto.FactoryResponseDto;
import projet.td3.model.dto.FactoryWithDistanceDto;
import projet.td3.model.entity.Factory;

@Mapper(componentModel = "spring")
public interface FactoryMapper {

    @Mapping(target = "address", ignore = true)
    FactoryResponseDto toResponseDto(Factory factory);

    @Mapping(target = "address", ignore = true)
    @Mapping(target = "distanceKm", ignore = true)
    FactoryWithDistanceDto toWithDistanceDto(Factory factory);
}
