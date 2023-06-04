package ru.itmo.profilepointservice.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.itmo.profilepointservice.domain.enumeration.Status;
import ru.itmo.profilepointservice.domain.enumeration.ProfileType;

public record UpdateProfileRequest(@NotBlank String name, @NotNull ProfileType profileType, @NotNull Status status) {
}
