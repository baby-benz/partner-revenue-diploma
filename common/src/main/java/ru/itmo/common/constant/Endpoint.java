package ru.itmo.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Endpoint {
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Profile {
        public static final String POST_NEW = "/profile";
    }
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Point {
        public static final String POST_NEW = "/point";
        public static final String GET_FULL = "/point/{pointId}";
        public static final String PUT = "/point/{pointId}";
        public static final String PATCH_CALC_SCHEME = "/point/{pointId}";
    }
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class CalcScheme {
        public static final String POST_NEW = "/calc-scheme";
    }
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class EventProcessor {
        public static final String GET_FULL = "/event/{eventId}";
    }
}
