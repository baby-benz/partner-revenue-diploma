package ru.itmo.profilepointservice.service.so.out;

import lombok.Builder;
import ru.itmo.profilepointservice.domain.enumeration.Status;
import ru.itmo.profilepointservice.domain.enumeration.PointType;

@Builder
public record RequestedFieldsPointSO(String pointId,
                                     String profileId,
                                     String name,
                                     PointType pointType,
                                     Status status,
                                     String calcSchemeId) {
}
