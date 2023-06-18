package pack.validators;

import dto.StatsParamDto;
import pack.exceptions.ValidationException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StatsParamValidator implements ConstraintValidator<StatsParamValidation, StatsParamDto> {

    @Override
    public boolean isValid(StatsParamDto dto, ConstraintValidatorContext constraintValidatorContext) {

        if (dto.getUnique() == null) {
            throw new ValidationException("unique parameter cannot be null");
        }
        if (dto.getStart() == null) {
            throw new ValidationException("start cannot be null");
        }
        if (dto.getEnd() == null) {
            throw new ValidationException("end cannot be null");
        }
        return true;
    }
}