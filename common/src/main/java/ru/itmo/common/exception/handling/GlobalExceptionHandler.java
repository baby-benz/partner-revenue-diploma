package ru.itmo.common.exception.handling;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.web.dto.response.DefaultApiErrorResponse;
import ru.itmo.common.web.dto.response.ValidationErrorResponse;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@ControllerAdvice
@ConditionalOnMissingBean(annotation = ControllerAdvice.class)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final MessageSource messageSource;

    @ExceptionHandler(HttpStatusCodeException.class)
    protected ResponseEntity<DefaultApiErrorResponse> handleHttpStatusCodeException(HttpStatusCodeException ex, WebRequest request) {
        List<String> errorMessages = new ArrayList<>();
        for (String messageCode : ex.getMessageCodes()) {
            errorMessages.add(messageSource.getMessage(messageCode, ex.getMessageArgs(), request.getLocale()));
        }
        return new ResponseEntity<>(
                new DefaultApiErrorResponse(
                        ex.getMessageCodes(),
                        errorMessages
                ),
                ex.getStatus()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ValidationErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        return new ResponseEntity<>(
                new ValidationErrorResponse(
                        ex.getConstraintViolations().stream().map(constraint -> {
                                    String fieldName = "undefined";
                                    for (Path.Node node : constraint.getPropertyPath()) {
                                        fieldName = node.getName();
                                    }
                                    return new ValidationErrorResponse.ConstraintViolationMessage(ex.getMessage(), fieldName);
                                }
                        ).toList()
                ),
                HttpStatus.BAD_REQUEST
        );
    }
}
