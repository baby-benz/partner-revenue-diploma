package ru.itmo.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.itmo.common.exception.cause.HttpErrorCause;

@Getter
public class HttpStatusCodeException extends RuntimeException {
    private final HttpErrorCause errorCause;
    private final String[] messageArgs;

    public HttpStatus getStatus() {
        return errorCause.getStatus();
    }

    public String getMessageCode() {
        return errorCause.getMessageCode();
    }

    public HttpStatusCodeException(HttpErrorCause errorCause, String... messageArgs) {
        this.errorCause = errorCause;
        this.messageArgs = messageArgs;
    }
}
