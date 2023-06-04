package ru.itmo.profilepointservice.service.so.in;

import ru.itmo.profilepointservice.domain.enumeration.Status;
import ru.itmo.profilepointservice.domain.enumeration.ProfileType;

public record CreateProfileSO(String name, ProfileType profileType, Status status) {
}
