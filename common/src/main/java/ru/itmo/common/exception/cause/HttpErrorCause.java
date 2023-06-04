package ru.itmo.common.exception.cause;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface HttpErrorCause {
    HttpStatus getStatus();
    List<String> getMessageCodes();

    interface Cause {
        String getMessageCode();
    }

    @RequiredArgsConstructor
    enum BadRequest implements Cause {
        INVALID_PROFILE_UUID_FORMAT("invalid-profile-uuid-format"),
        INVALID_POINT_UUID_FORMAT("invalid-point-uuid-format");

        private final String errorMessageCode;

        @Override
        public String getMessageCode() {
            return this.errorMessageCode;
        }
    }

    @RequiredArgsConstructor
    enum NotFound implements Cause {
        PROFILE_NOT_FOUND("profile-not-found"),
        POINT_NOT_FOUND("point-not-found"),
        POINT_BY_ID_AND_PROFILE_ID_NOT_FOUND("point-by-id-and-profile-id-not-found"),
        EVENT_NOT_FOUND("event-not-found"),
        CALC_SCHEME_NOT_FOUND("calc-scheme-not-found"),
        REWARD_NOT_FOUND("reward-not-found");

        private final String errorMessageCode;

        @Override
        public String getMessageCode() {
            return this.errorMessageCode;
        }
    }
}
