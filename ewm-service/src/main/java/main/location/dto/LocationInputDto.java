package main.location.dto;

import lombok.Data;
import main.access.Access;

@Data
public class LocationInputDto {

    private Long id;
    private Double lon;
    private Double lat;
    private String place;
    private Integer radius;
}
