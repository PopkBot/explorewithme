package main.compilation.validator;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CompilationUpdateValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CompilationUpdate {
    String message() default "{Compilation is invalid for patching}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


