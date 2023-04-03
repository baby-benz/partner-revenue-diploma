package ru.itmo.calcscheme.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CalcRulesValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCalcRules {
    String message() default "Calculation rules should be sorted by amount";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
