package projet.td.models.dao;


import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToMany(mappedBy="product")
    @JoinColumn(name = "product_id")
    private Set<Orders_details> Orders_details;

    @OneToMany(mappedBy="product")
    @JoinColumn(name = "product_id")
    private Set<Notice> Notices  ;


}
