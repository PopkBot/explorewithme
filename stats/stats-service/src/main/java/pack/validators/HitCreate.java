package pack.validators;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = HitCreateValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface HitCreate {
    String message() default "{Hit is invalid for adding}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


