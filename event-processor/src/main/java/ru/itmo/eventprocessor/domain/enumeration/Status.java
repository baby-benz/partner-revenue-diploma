package ru.itmo.eventprocessor.domain.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    NOT_PROCESSED("N"),
    PROCESSED("P"),
    UNPROCESSABLE("U");

    private final String statusCode;

    public static Status fromStatusCode(String statusCode) {
        return switch (statusCode) {
            case "N" -> Status.NOT_PROCESSED;
            case "P" -> Status.PROCESSED;
            case "U" -> Status.UNPROCESSABLE;
            default -> throw new IllegalArgumentException("StatusCode [" + statusCode + "] not supported.");
        };
    }
}
