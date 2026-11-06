package projet.td3.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "factories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Factory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer production;

    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Version
    private Long version;
}
