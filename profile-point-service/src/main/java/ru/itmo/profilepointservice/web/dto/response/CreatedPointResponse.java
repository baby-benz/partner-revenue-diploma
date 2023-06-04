package ru.itmo.profilepointservice.web.dto.response;

import ru.itmo.profilepointservice.domain.enumeration.Status;
import ru.itmo.profilepointservice.domain.enumeration.PointType;

public record CreatedPointResponse(String pointId,
                                   String profileId,
                                   String name,
                                   PointType pointType,
                                   Status status,
                                   String calcSchemeId) {
}
