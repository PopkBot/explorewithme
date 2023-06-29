package main.event.validator;


import constants.FormatConstants;
import dto.HitInputDto;
import main.event.dto.EventInputDto;
import main.exceptions.ValidationException;
import org.apache.commons.validator.routines.InetAddressValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class EventCreateValidator implements ConstraintValidator<EventCreate, EventInputDto> {

    @Override
    public boolean isValid(EventInputDto event, ConstraintValidatorContext constraintValidatorContext) {

        if(event.getTitle() == null || event.getTitle().strip().length()<3 || event.getTitle().strip().length()>120){
            throw new ValidationException("Title cannot be shorter then 3 characters or longer then 120 characters");
        }
        if(event.getDescription() == null || event.getDescription().strip().length()<20 ||
                event.getDescription().strip().length()>7000){
            throw new ValidationException("Description cannot be shorter then 20 characters or longer then 7000 characters");
        }
        if(event.getAnnotation() == null || event.getAnnotation().strip().length()<20 ||
                event.getAnnotation().strip().length()>2000){
            throw new ValidationException("Annotation cannot be shorter then 20 characters or longer then 2000 characters");
        }

        if(event.getEventDate() == null){
            throw new ValidationException("Event date cannot be null");
        }else if(LocalDateTime.parse(event.getEventDate(), FormatConstants.DATE_TIME_FORMATTER)
                .atZone(ZoneId.systemDefault()).isBefore(ZonedDateTime.now(ZoneId.systemDefault()))){
            throw new ValidationException("Event date cannot be in the past");
        }
        if(event.getLocation() == null){
            throw new ValidationException("Location cannot be null");
        }
        return true;
    }
}


