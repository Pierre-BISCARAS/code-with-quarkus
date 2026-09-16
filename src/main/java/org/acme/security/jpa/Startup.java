package org.acme.security.jpa;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.acme.models.dao.Customer;
import org.acme.repositories.CustomerRepository;

@Singleton
public class Startup {

    @Inject
    CustomerRepository repository;

    @Transactional
    public void loadUsers(@Observes StartupEvent evt) {
        // reset and load all test users
        repository.deleteAll();
        repository.persist(newCustomer("admin", "admin", "admin"));
        repository.persist(newCustomer("user", "user", "user"));
    }

    private Customer newCustomer(String email, String password, String role) {
        Customer customer = new Customer();
        customer.email = email;
        customer.password = BcryptUtil.bcryptHash(password);
        customer.role = role;
        customer.address = "Amiens";
        return customer;
    }
}
