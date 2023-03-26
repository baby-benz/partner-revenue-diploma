package ru.itmo.point.web.dto.response;

import ru.itmo.common.domain.enumeration.Status;
import ru.itmo.point.domain.enumeration.PointType;

public record CreatedPointResponse(String pointId,
                                   String profileId,
                                   String name,
                                   PointType pointType,
                                   Status status) {
}
