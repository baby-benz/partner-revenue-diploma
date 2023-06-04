package ru.itmo.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InternalEndpoint {
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Profile {
        public static final String HEAD_CHECK_PROFILE = "/internal/profile/{profileId}/check";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Point {
        public static final String HEAD_CHECK_POINT = "/internal/point/{pointId}/check";
        public static final String GET_REQUESTED_POINT_FIELDS = "/internal/point/{pointId}";
        public static final String GET_POINT_IDS_WITH_PROFILE_ID = "/internal/point";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class CalcScheme {
        public static final String HEAD_CHECK_CALC_SCHEME = "/internal/{calcSchemeId}/check";
        public static final String GET_CALC_SCHEME = "/internal/{calcSchemeId}";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class RewardCalculator {
        public static final String GET_TOTALS = "/internal/totals";
        public static final String GET_HISTORY_TOTALS = "/internal/totals/history";
        public static final String GET_AVG = "/internal/avg";
        public static final String GET_EXTREMES = "/internal/extremes";
    }
}
