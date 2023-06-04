package ru.itmo.common.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UUIDValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUUID {
    String message() default ValidProperty.UUID;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
