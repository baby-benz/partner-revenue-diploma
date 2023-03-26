package ru.itmo.profile.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InternalEndpoint {
    public static final String GET_PROFILE_EXISTS = "/profile/{profileId}/exists";
}
