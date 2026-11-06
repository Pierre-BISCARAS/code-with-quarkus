package projet.td3.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FactoryResponseDto {

    private Long id;
    private String name;
    private Integer production;
    private Integer stock;
    private Double latitude;
    private Double longitude;
    private String address;
}
