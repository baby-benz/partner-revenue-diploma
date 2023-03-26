package ru.itmo.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Endpoint {
    public final class Profile {
        public static final String POST_NEW = "/profile";
    }
    public final class Point {
        public static final String POST_NEW = "/point";
        public static final String GET_FULL = "/point/{pointId}";
        public static final String PUT = "/point/{pointId}";
    }
}
