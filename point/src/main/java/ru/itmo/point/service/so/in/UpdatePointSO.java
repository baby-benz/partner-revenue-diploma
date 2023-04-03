package ru.itmo.point.service.so.in;

import ru.itmo.common.domain.enumeration.Status;
import ru.itmo.point.domain.enumeration.PointType;

public record UpdatePointSO(String pointId,
                            String profileId,
                            String name,
                            PointType pointType,
                            Status status,
                            String calcSchemeId) {
}
