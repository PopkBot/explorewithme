package main.compilation.validator;


import main.category.dto.CategoryInputDto;
import main.compilation.dto.CompilationInputDto;
import main.exceptions.ValidationException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CompilationCreateValidator implements ConstraintValidator<CompilationCreate, CompilationInputDto> {

    @Override
    public boolean isValid(CompilationInputDto compilation, ConstraintValidatorContext constraintValidatorContext) {

       if(compilation.getPinned() == null){
           throw new ValidationException("Pinned cannot be null");
       }
       if(compilation.getTitle() == null || compilation.getTitle().isBlank()){
           throw new ValidationException("Title cannot be blank");
       }
        return true;
    }
}


