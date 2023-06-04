package ru.itmo.common.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NullableUUIDValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNullableUUID {
    String message() default "Wrong date range";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
