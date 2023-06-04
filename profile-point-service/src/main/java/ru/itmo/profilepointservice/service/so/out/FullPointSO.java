package ru.itmo.profilepointservice.service.so.out;

import ru.itmo.profilepointservice.domain.enumeration.Status;
import ru.itmo.profilepointservice.domain.enumeration.PointType;

public record FullPointSO(String pointId,
                          String profileId,
                          String name,
                          PointType pointType,
                          Status status,
                          String calcSchemeId) {
}
