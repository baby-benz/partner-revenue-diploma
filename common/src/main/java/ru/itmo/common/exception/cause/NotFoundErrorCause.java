package ru.itmo.common.exception.cause;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum NotFoundErrorCause implements HttpErrorCause {
    PROFILE_NOT_FOUND("profile-not-found"),
    POINT_NOT_FOUND("point-not-found");

    private final String errorMessageCode;

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getMessageCode() {
        return this.errorMessageCode;
    }
}
