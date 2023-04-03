package ru.itmo.common.exception.cause;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum NotFoundErrorCause implements HttpErrorCause {
    PROFILE_NOT_FOUND("profile-not-found"),
    POINT_NOT_FOUND("point-not-found"),
    POINT_BY_ID_AND_PROFILE_ID_NOT_FOUND("point-by-id-and-profile-id-not-found"),
    EVENT_NOT_FOUND("event-not-found"),
    CALC_SCHEME_NOT_FOUND("calc-scheme-not-found");

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
