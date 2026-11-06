package projet.td3.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import projet.td3.model.dto.StoreResponseDto;
import projet.td3.model.entity.Store;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    @Mapping(target = "address", ignore = true)
    StoreResponseDto toResponseDto(Store store);
}
