package ru.itmo.common.exception.handling;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.web.dto.response.DefaultApiErrorResponse;

@RequiredArgsConstructor
@ControllerAdvice
@ConditionalOnMissingBean(annotation = ControllerAdvice.class)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final MessageSource messageSource;

    @ExceptionHandler(HttpStatusCodeException.class)
    protected ResponseEntity<DefaultApiErrorResponse> handleHttpStatusCodeException(HttpStatusCodeException ex, WebRequest request) {
        String errorMessage = messageSource.getMessage(ex.getMessageCode(), ex.getMessageArgs(), request.getLocale());
        return new ResponseEntity<>(
                new DefaultApiErrorResponse(
                        ex.getMessageCode(),
                        errorMessage
                ),
                ex.getStatus()
        );
    }
}
