package org.acme.models.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "email")
    @Username
    public String email;

    @Column(name = "password")
    @Password
    public String password;

    @Roles
    public String role = "user";

    @Column(name = "name")
    public String name;

    @Column(name = "address")
    public String address;

    @Column(name = "balance")
    public BigDecimal balance = BigDecimal.valueOf(500.00);

    @Column(name = "paperclipStock")
    public Integer paperclipStock = 0;
}
