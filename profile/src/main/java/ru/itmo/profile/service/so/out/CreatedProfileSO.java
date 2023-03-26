package ru.itmo.profile.service.so.out;

import ru.itmo.common.domain.enumeration.Status;
import ru.itmo.profile.domain.enumeration.ProfileType;

public record CreatedProfileSO(String profileId, String name, ProfileType profileType, Status status) {
}
