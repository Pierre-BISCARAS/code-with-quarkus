package projet.td2.models.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@AllArgsConstructor @NoArgsConstructor
@Builder
@Getter
@Setter
public class FactoryDto {

    @NotEmpty
    private String name;

    @NotEmpty
    @PositiveOrZero
    private Integer production;

    @NotEmpty
    private String stock;

    @NotEmpty
    private double latitude;

    @NotEmpty
    private double longitude;
}