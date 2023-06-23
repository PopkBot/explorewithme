package main.category.validator;


import main.category.dto.CategoryInputDto;
import main.exceptions.ValidationException;
import main.user.dto.UserInputDto;
import org.apache.commons.validator.routines.EmailValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CategoryCreateValidator implements ConstraintValidator<CategoryCreate, CategoryInputDto> {

    @Override
    public boolean isValid(CategoryInputDto category, ConstraintValidatorContext constraintValidatorContext) {

        if (category.getName() == null || category.getName().isBlank()) {
            throw new ValidationException("name cannot be blank "+category.toString());
        }

        return true;
    }
}


