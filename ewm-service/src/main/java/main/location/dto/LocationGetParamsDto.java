package main.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.access.Access;
import main.exceptions.ValidationException;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationGetParamsDto {
    Double lat;
    Double lon;
    Integer radius;
    String country;
    String city;
    String place;
    Integer from;
    Integer size;
    Access access;

    public void validate() {
        if ((lat != null && lon == null) || (lon != null && radius == null) || (radius != null && lat == null)) {
            throw new ValidationException("Latitude, longitude and radius cannot be null");
        }
        if (lat != null && lon != null && radius != null) {
            if (Math.abs(lat) > 90) {
                throw new ValidationException("Latitude must be in range [-90,90]");
            }
            if (Math.abs(lon) > 180) {
                throw new ValidationException("Longitude must be in range [-180,180]");
            }
            if (radius <= 0) {
                throw new ValidationException("Radius must be greater then 0m");
            }
        }
        if (place != null && place.isBlank()) {
            throw new ValidationException("Place name cannot be blank");
        }
        if (country != null && country.isBlank()) {
            throw new ValidationException("Country name cannot be blank");
        }
        if (city != null && city.isBlank()) {
            throw new ValidationException("City name cannot be blank");
        }

    }
}
