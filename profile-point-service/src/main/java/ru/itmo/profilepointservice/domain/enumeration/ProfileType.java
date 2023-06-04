package ru.itmo.profilepointservice.domain.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProfileType {
    DISTRIBUTOR("D"),
    PAYMENT_PARTNER("P");

    private final String profileTypeCode;

    public static ProfileType fromProfileTypeCode(String profileTypeCode) {
        return switch (profileTypeCode) {
            case "D" -> ProfileType.DISTRIBUTOR;
            case "P" -> ProfileType.PAYMENT_PARTNER;
            default -> throw new IllegalArgumentException("ProfileTypeCode [" + profileTypeCode + "] not supported.");
        };
    }
}
