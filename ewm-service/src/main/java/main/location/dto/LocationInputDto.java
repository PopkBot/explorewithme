package main.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationInputDto {

    private Long id;
    private Double lon;
    private Double lat;
    private String place;
    private Integer radius;
}
