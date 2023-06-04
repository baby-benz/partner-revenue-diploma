package ru.itmo.profilepointservice.service.so.out;

import ru.itmo.profilepointservice.domain.enumeration.Status;
import ru.itmo.profilepointservice.domain.enumeration.ProfileType;

public record UpdatedProfileSO(String profileId, String name, ProfileType profileType, Status status) {
}
