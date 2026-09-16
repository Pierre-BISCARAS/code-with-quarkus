package org.acme.services.customer;

import org.acme.models.dao.Customer;
import org.acme.models.dto.CustomerRequest;

public interface CustomerService {
    Customer register(CustomerRequest request);
    Customer getByEmail(String email);
}
