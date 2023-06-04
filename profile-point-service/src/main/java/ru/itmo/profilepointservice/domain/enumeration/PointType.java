package ru.itmo.profilepointservice.domain.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointType {
    PAYMENT_RECEPTION("P"),
    PRODUCT_SELLING("S");

    private final String pointTypeCode;

    public static PointType fromPointTypeCode(String pointTypeCode) {
        return switch (pointTypeCode) {
            case "P" -> PointType.PAYMENT_RECEPTION;
            case "S" -> PointType.PRODUCT_SELLING;
            default -> throw new IllegalArgumentException("PointTypeCode [" + pointTypeCode + "] not supported.");
        };
    }
}
