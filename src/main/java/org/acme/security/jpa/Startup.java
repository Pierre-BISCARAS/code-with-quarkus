package org.acme.security.jpa;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import io.quarkus.runtime.StartupEvent;
import org.acme.models.dao.Customer;

@Singleton
public class Startup {
    @Transactional
    public void loadUsers(@Observes StartupEvent evt) {
        // reset and load all test users
        Customer.deleteAll();
        Customer.add("admin", "admin", "admin", "Amiens");
        Customer.add("user", "user", "user", "Amiens");
    }
}