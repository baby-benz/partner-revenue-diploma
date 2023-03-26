package ru.itmo.profile.service.so.in;

import ru.itmo.common.domain.enumeration.Status;
import ru.itmo.profile.domain.enumeration.ProfileType;

public record CreateProfileSO(String name, ProfileType profileType, Status status) {
}
