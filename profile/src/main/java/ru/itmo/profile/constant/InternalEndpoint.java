package ru.itmo.profile.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InternalEndpoint {
    public static final String HEAD_CHECK_PROFILE = "/profile-internal/{profileId}/check";
}
