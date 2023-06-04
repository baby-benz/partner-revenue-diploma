package ru.itmo.profilepointservice.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.itmo.profilepointservice.domain.enumeration.Status;
import ru.itmo.profilepointservice.domain.enumeration.PointType;
import ru.itmo.common.web.validation.ValidUUID;

public record UpdatePointRequest(@ValidUUID @NotBlank String profileId,
                                 @NotBlank String name,
                                 @NotNull PointType pointType,
                                 Status status,
                                 String calcSchemeId) {
}
