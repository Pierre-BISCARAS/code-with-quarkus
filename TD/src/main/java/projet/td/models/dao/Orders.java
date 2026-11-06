package projet.td.models.dao;

import jakarta.persistence.*;
import org.aspectj.weaver.ast.Not;


@Entity
@Table(name="ORDERS")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="notice_id", referencedColumnName = "id")
    private Notice notice;

    @ManyToOne
    @JoinColumn(name="account", nullable=false)
    private Account account;


}
