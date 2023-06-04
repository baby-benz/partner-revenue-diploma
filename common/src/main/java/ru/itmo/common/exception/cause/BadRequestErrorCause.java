package ru.itmo.common.exception.cause;

import org.springframework.http.HttpStatus;

import java.util.List;

public class BadRequestErrorCause implements HttpErrorCause {
    private final List<String> errorMessageCodes;

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public List<String> getMessageCodes() {
        return this.errorMessageCodes;
    }

    public BadRequestErrorCause(List<BadRequest> causes) {
        this.errorMessageCodes = causes.stream().map(Cause::getMessageCode).toList();
    }
}
