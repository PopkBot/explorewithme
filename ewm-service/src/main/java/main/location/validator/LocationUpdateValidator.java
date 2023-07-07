package main.location.validator;


import main.compilation.dto.CompilationUpdateDto;
import main.exceptions.ValidationException;
import main.location.dto.LocationInputDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LocationUpdateValidator implements ConstraintValidator<LocationUpdate, LocationInputDto> {

    @Override
    public boolean isValid(LocationInputDto location, ConstraintValidatorContext constraintValidatorContext) {


        if(location.getLat() != null && Math.abs(location.getLat())>90){
            throw new ValidationException("Latitude must be in range [-90,90]");
        }
        if(location.getLon() != null && Math.abs(location.getLon())>180){
            throw new ValidationException("Longitude must be in range [-180,180]");
        }
        if(location.getRadius() != null && location.getRadius()<=0){
            throw new ValidationException("Radius must be greater then 0m");
        }
        if(location.getPlace() != null && location.getPlace().isBlank()){
            throw new ValidationException("Place name cannot be blank");
        }
        return true;
    }
}


