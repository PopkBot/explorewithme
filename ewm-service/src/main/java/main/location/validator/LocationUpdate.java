package main.location.validator;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = LocationUpdateValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface LocationUpdate {
    String message() default "{Location is invalid for patching}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


