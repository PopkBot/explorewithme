package main.user.validator;


import main.exceptions.ValidationException;
import main.user.dto.UserInputDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserCreateValidator implements ConstraintValidator<UserCreate, UserInputDto> {

    @Override
    public boolean isValid(UserInputDto user, ConstraintValidatorContext constraintValidatorContext) {

        if (user.getName() == null || user.getName().length() < 2 || user.getName().length() > 250) {
            throw new ValidationException("name cannot be shorter then 2 characters and longer then 250 characters");
        }
        if (user.getName().isBlank()) {
            throw new ValidationException("name cannot be blank");
        }

        if (user.getEmail() == null) {
            throw new ValidationException("email is invalid");
        }
        if (user.getEmail().length() < 6 || user.getEmail().length() > 254) {
            throw new ValidationException("email cannot be shorter then 6 characters and longer then 254 characters");
        }
        int atIndex = user.getEmail().indexOf("@");
        int domainLength = user.getEmail().indexOf(".") - atIndex - 1;
        if (atIndex > 64) {
            throw new ValidationException("local part of email cannot be longer then 64 characters");
        }
        if (domainLength > 63) {
            throw new ValidationException("domain part of email cannot be longer then 64 characters");
        }
        return true;
    }
}


