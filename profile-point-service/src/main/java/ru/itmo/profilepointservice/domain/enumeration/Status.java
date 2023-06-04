package ru.itmo.profilepointservice.domain.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    INACTIVE("I"),
    ACTIVE("A"),
    SUSPENDED("S");

    private final String statusCode;

    public static Status fromStatusCode(String statusCode) {
        return switch (statusCode) {
            case "I" -> Status.INACTIVE;
            case "A" -> Status.ACTIVE;
            case "S" -> Status.SUSPENDED;
            default -> throw new IllegalArgumentException("StatusCode [" + statusCode + "] not supported.");
        };
    }
}
