package ru.itmo.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Endpoint {
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Profile {
        public static final String POST_NEW = "/profile";
        public static final String GET_FULL = "/profile/{profileId}";
        public static final String PUT = "/profile/{profileId}";
        public static final String PATCH_SUSPEND = "/profile/{profileId}";
    }
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Point {
        public static final String POST_NEW = "/point";
        public static final String GET_FULL = "/point/{pointId}";
        public static final String PUT = "/point/{pointId}";
        public static final String PATCH_CALC_SCHEME = "/point/{pointId}/calc-scheme";
        public static final String PATCH_STATUS = "/point/{pointId}/status";
    }
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class CalcScheme {
        public static final String POST_NEW = "/";
        public static final String GET_FULL = "/{calcSchemeId}";
    }
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class EventProcessor {
        public static final String GET_FULL = "/{eventId}";
    }
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class RewardCalculator {
        public static final String GET_FULL = "/{rewardId}";
    }
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Report {
        public static final String GET_TOTALS = "/basic/totals";
        public static final String GET_AVG_REWARD = "/analytical/avg";
        public static final String GET_MAX_REWARD = "/analytical/max";
        public static final String GET_MIN_REWARD = "/analytical/min";
        public static final String GET_EXTREME_REWARD = "/analytical/extreme";
    }
}
