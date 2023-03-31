package ru.itmo.point.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InternalEndpoint {
    public static final String HEAD_CHECK_POINT = "/point-internal/{pointId}/check";
}
