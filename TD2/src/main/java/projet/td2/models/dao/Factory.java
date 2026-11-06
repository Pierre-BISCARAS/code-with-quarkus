package projet.td2.models.dao;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor @NoArgsConstructor
@Builder @Getter
@Setter
public class Factory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    private Integer production;

    @Column
    private Integer stock;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    public void addProduction(int nombre){
        production += nombre;
    }

}