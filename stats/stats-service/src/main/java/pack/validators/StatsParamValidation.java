package pack.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StatsParamValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StatsParamValidation {
    String message() default "{parameters aro invalid for request}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}