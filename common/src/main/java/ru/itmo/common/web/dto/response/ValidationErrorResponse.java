package ru.itmo.common.web.dto.response;

import java.util.List;

public record ValidationErrorResponse(List<ConstraintViolationMessage> constraintViolationMessages) {
    public record ConstraintViolationMessage(String validationErrorMessageCode, String validatedField) {
    }
}
