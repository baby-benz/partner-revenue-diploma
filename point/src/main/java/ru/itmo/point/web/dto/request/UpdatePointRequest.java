package ru.itmo.point.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.itmo.common.domain.enumeration.Status;
import ru.itmo.point.domain.enumeration.PointType;

public record UpdatePointRequest(@NotBlank String profileId,
                                 @NotBlank String name,
                                 @NotNull PointType pointType,
                                 Status status,
                                 String calcSchemeId) {
}
