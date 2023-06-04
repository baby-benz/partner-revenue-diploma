package ru.itmo.profilepointservice.service.so.in;

import ru.itmo.profilepointservice.domain.enumeration.Status;
import ru.itmo.profilepointservice.domain.enumeration.PointType;

public record UpdatePointSO(String pointId,
                            String profileId,
                            String name,
                            PointType pointType,
                            Status status,
                            String calcSchemeId) {
}
