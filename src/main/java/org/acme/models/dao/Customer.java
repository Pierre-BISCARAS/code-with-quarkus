package org.acme.models.dao;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;

import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@UserDefinition
@Table(name = "test_user")
@Entity
@Setter @Getter
public class Customer extends PanacheEntity {
    @GeneratedValue
    public Long id;

    @Column(name = "email")
    @Username
    public String email;

    @Column(name = "password")
    @Password()
    public String password;

    @Roles()
    public String role = "user";

    @Column(name = "name")
    public String name;

    @Column(name = "address")
    public String address;

    @Column(name = "balance")
    public BigDecimal balance = BigDecimal.valueOf(500.00);

    @Column(name = "paperclipStock")
    public Integer paperclipStock = 0;

    /**
     * Adds a new user to the database
     * @param email the email
     * @param password the unencrypted password (it is encrypted with bcrypt)
     * @param role the comma-separated roles
     * @param address the full address
     */
    public static void add(String email, String password, String role, String address) {
        Customer customer = new Customer();
        customer.email = email;
        customer.password = BcryptUtil.bcryptHash(password);
        customer.role = role;
        customer.persist();
    }

}