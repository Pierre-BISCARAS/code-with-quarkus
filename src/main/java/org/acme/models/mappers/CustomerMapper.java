package org.acme.models.mappers;

import org.acme.models.dao.Customer;
import org.acme.models.dto.CustomerResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface CustomerMapper {
    CustomerResponse toResponse(Customer customer);
}
