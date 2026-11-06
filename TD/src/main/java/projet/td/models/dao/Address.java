package projet.td.models.dao;


import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;


    @OneToOne(mappedBy = "address")
    private Account account;

    @OneToMany
    @JoinColumn(name = "orders_id")
    private Set<Account> accounts;

}