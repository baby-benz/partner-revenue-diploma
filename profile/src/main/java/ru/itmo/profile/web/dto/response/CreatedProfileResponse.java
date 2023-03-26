package ru.itmo.profile.web.dto.response;

import ru.itmo.common.domain.enumeration.Status;
import ru.itmo.profile.domain.enumeration.ProfileType;

public record CreatedProfileResponse(String profileId, String name, ProfileType profileType, Status status) {
}
