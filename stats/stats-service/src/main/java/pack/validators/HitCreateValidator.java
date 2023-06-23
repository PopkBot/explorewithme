package pack.validators;


import dto.HitInputDto;
import org.apache.commons.validator.routines.InetAddressValidator;
import pack.exceptions.ValidationException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class HitCreateValidator implements ConstraintValidator<HitCreate, HitInputDto> {

    @Override
    public boolean isValid(HitInputDto hit, ConstraintValidatorContext constraintValidatorContext) {

        if (hit.getApp() == null || hit.getApp().isBlank()) {
            throw new ValidationException("app cannot be blank");
        }
        if (hit.getUri() == null || hit.getUri().isBlank()) {
            throw new ValidationException("uri cannot be blank");
        }
        if (!InetAddressValidator.getInstance().isValid(hit.getIp())) {
            throw new ValidationException("IP does not mach format");
        }
        if (hit.getTimestamp() == null) {
            throw new ValidationException("timestamp cannot be null");
        }
        return true;
    }
}


