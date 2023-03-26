package ru.itmo.point.web.dto.request;

import ru.itmo.common.domain.enumeration.Status;
import ru.itmo.point.domain.enumeration.PointType;

public record UpdatePointRequest(String profileId, String name, PointType pointType, Status status) {
}
