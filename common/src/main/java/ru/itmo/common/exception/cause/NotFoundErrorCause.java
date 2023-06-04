package ru.itmo.common.exception.cause;

import org.springframework.http.HttpStatus;

import java.util.List;

public class NotFoundErrorCause implements HttpErrorCause {
    private final List<String> errorMessageCodes;

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public List<String> getMessageCodes() {
        return this.errorMessageCodes;
    }

    public NotFoundErrorCause(List<NotFound> causes) {
        this.errorMessageCodes = causes.stream().map(Cause::getMessageCode).toList();
    }
}
