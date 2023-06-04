package ru.itmo.profilepointservice.web.dto.response;

import ru.itmo.profilepointservice.domain.enumeration.Status;
import ru.itmo.profilepointservice.domain.enumeration.ProfileType;

public record FullProfileResponse(String profileId, String name, ProfileType profileType, Status status) {
}
