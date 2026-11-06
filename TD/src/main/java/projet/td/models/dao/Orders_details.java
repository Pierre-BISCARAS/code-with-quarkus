package projet.td.models.dao;


import jakarta.persistence.*;



public class Orders_details {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name="orders_id", nullable=false)
    private Orders orders;

    @ManyToOne
    @JoinColumn(name="product_id", nullable=false)
    private Product product;

}
