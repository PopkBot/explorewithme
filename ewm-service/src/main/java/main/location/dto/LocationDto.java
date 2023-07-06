package main.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    private Long id;
    private Double lat;
    private Double lon;
    private Integer radius;
    private String country;
    private String city;
    private String place;
}
