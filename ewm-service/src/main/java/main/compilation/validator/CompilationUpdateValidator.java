package main.compilation.validator;


import main.category.dto.CategoryInputDto;
import main.compilation.dto.CompilationUpdateDto;
import main.exceptions.ValidationException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CompilationUpdateValidator implements ConstraintValidator<CompilationUpdate, CompilationUpdateDto> {

    @Override
    public boolean isValid(CompilationUpdateDto compilation, ConstraintValidatorContext constraintValidatorContext) {

        if (compilation.getTitle() != null && compilation.getTitle().isBlank()) {
            throw new ValidationException("Title cannot be blank");
        }
        return true;
    }
}


