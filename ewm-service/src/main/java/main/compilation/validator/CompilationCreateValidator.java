package main.compilation.validator;


import main.compilation.dto.CompilationInputDto;
import main.exceptions.ValidationException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CompilationCreateValidator implements ConstraintValidator<CompilationCreate, CompilationInputDto> {

    @Override
    public boolean isValid(CompilationInputDto compilation, ConstraintValidatorContext constraintValidatorContext) {

        if (compilation.getTitle() == null || compilation.getTitle().length() > 50) {
            throw new ValidationException("Title cannot be longer then 50 characters");
        }
        if (compilation.getTitle().isBlank()) {
            throw new ValidationException("Title cannot be blank");
        }
        return true;
    }
}


