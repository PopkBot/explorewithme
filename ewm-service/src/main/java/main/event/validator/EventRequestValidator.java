package main.event.validator;


import constants.FormatConstants;
import main.event.dto.GetEventsParamsDto;
import main.exceptions.ValidationException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class EventRequestValidator implements ConstraintValidator<ValidateEventRequest, GetEventsParamsDto> {

    @Override
    public boolean isValid(GetEventsParamsDto dto, ConstraintValidatorContext constraintValidatorContext) {

        if (dto.getRangeEnd() != null && dto.getRangeStart() != null) {
            ZonedDateTime start = LocalDateTime.parse(dto.getRangeStart(), FormatConstants.DATE_TIME_FORMATTER)
                    .atZone(ZoneId.systemDefault());
            ZonedDateTime end = LocalDateTime.parse(dto.getRangeEnd(), FormatConstants.DATE_TIME_FORMATTER)
                    .atZone(ZoneId.systemDefault());
            if (start.isBefore(end)) {
                throw new ValidationException("End date cannot be before start");
            }
        }
        return true;
    }
}


