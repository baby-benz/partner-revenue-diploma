package ru.itmo.common.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class NullableUUIDValidator implements ConstraintValidator<ValidNullableUUID, String> {
    @Override
    public void initialize(ValidNullableUUID validNullableUUID) {
    }

    @Override
    public boolean isValid(String uuidFrom, ConstraintValidatorContext cxt) {
        if (uuidFrom == null) {
            return true;
        }

        Pattern UUID_REGEX =
                Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

        return UUID_REGEX.matcher(uuidFrom).matches();
    }
}
