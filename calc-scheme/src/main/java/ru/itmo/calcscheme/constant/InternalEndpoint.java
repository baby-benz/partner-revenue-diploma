package ru.itmo.calcscheme.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InternalEndpoint {
    public static final String HEAD_CHECK_CALC_SCHEME = "/calc-scheme-internal/{calcSchemeId}/check";
}

