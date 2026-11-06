package projet.td3.model.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FactoryUpdateDto {

    private String name;

    @PositiveOrZero
    private Integer production;

    @PositiveOrZero
    private Integer stock;

    private String address;
}
