package main.user.validator;


import main.exceptions.ValidationException;
import main.user.dto.UserInputDto;
import org.apache.commons.validator.routines.EmailValidator;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserCreateValidator implements ConstraintValidator<UserCreate, UserInputDto> {

    @Override
    public boolean isValid(UserInputDto user, ConstraintValidatorContext constraintValidatorContext) {

        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("name cannot be blank");
        }
        if (user.getEmail() == null || EmailValidator.getInstance().isValid(user.getEmail())) {
            throw new ValidationException("email is invalid");
        }
        return true;
    }
}


