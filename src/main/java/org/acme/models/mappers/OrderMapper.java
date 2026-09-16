package org.acme.models.mappers;

import org.acme.models.dao.Order;
import org.acme.models.dto.OrderResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface OrderMapper {
    OrderResponse toResponse(Order order);
}
