package ru.itmo.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.itmo.common.exception.cause.HttpErrorCause;

import java.util.List;

@Getter
public class HttpStatusCodeException extends RuntimeException {
    private final HttpErrorCause errorCause;
    private final String[] messageArgs;

    public HttpStatus getStatus() {
        return errorCause.getStatus();
    }

    public List<String> getMessageCodes() {
        return errorCause.getMessageCodes();
    }

    public HttpStatusCodeException(HttpErrorCause errorCause, String... messageArgs) {
        this.errorCause = errorCause;
        this.messageArgs = messageArgs;
    }
}
