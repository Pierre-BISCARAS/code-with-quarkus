package org.acme.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.models.dao.Customer;

@ApplicationScoped
public class CustomerRepository implements PanacheRepository<Customer> {

    public Customer findByEmail(String email) {
        return find("email", email).firstResult();
    }
}
