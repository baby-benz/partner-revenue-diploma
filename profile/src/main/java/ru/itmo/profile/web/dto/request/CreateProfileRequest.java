package ru.itmo.profile.web.dto.request;

import ru.itmo.common.domain.enumeration.Status;
import ru.itmo.profile.domain.enumeration.ProfileType;

public record CreateProfileRequest(String name, ProfileType profileType, Status status) {
}
