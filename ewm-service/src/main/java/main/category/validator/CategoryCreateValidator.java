package main.category.validator;


import main.category.dto.CategoryInputDto;
import main.exceptions.ValidationException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CategoryCreateValidator implements ConstraintValidator<CategoryCreate, CategoryInputDto> {

    @Override
    public boolean isValid(CategoryInputDto category, ConstraintValidatorContext constraintValidatorContext) {

        if (category.getName() == null || category.getName().length() > 50) {
            throw new ValidationException("name cannot be longer then 50 characters");
        }
        if (category.getName().isBlank()) {
            throw new ValidationException("name cannot be blank");
        }
        return true;
    }
}


