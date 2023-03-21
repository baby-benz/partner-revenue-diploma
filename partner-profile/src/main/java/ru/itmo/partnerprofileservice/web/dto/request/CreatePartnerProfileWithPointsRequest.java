package ru.itmo.partnerprofileservice.web.dto.request;

import ru.itmo.partnerprofileservice.domain.enumeration.PartnerPointType;
import ru.itmo.partnerprofileservice.domain.enumeration.PartnerType;

public record CreatePartnerProfileWithPointsRequest(String name,
                                                    PartnerType partnerType,
                                                    PartnerPointRequest[] points) {
    private record PartnerPointRequest(String name, PartnerPointType partnerPointType) {
    }
}
