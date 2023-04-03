package ru.itmo.profile.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.itmo.common.domain.enumeration.Status;
import ru.itmo.profile.domain.enumeration.ProfileType;

public record CreateProfileRequest(@NotBlank String name, @NotNull ProfileType profileType, Status status) {
}
