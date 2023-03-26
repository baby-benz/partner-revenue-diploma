package ru.itmo.point.service.so.out;

import ru.itmo.common.domain.enumeration.Status;
import ru.itmo.point.domain.enumeration.PointType;

public record UpdatedPointSO(String pointId,
                             String profileId,
                             String name,
                             PointType pointType,
                             Status status) {
}
