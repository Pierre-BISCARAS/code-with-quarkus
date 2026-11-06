package projet.td.models.dao;


import jakarta.persistence.*;

@Entity
@Table(name = "NOTICE")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @OneToOne(mappedBy = "notice")
    private Orders orders;

}
