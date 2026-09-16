package org.acme.services.customer;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.models.dao.Customer;
import org.acme.models.dto.CustomerRequest;
import org.acme.repositories.CustomerRepository;

@ApplicationScoped
public class CustomerServiceImp implements CustomerService {

    @Inject
    CustomerRepository repository;

    @Override
    @Transactional
    public Customer register(CustomerRequest request) {
        Customer customer = new Customer();
        customer.email = request.email();
        customer.password = BcryptUtil.bcryptHash(request.password());
        customer.role = "user";
        customer.address = request.address();
        repository.persist(customer);
        return customer;
    }

    @Override
    public Customer getByEmail(String email) {
        return repository.findByEmail(email);
    }
}
