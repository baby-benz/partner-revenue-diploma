package ru.itmo.registrationservice.web.dto.request;

import ru.itmo.registrationservice.domain.enumeration.PartnerPointType;
import ru.itmo.registrationservice.domain.enumeration.PartnerType;

public record CreatePartnerProfileWithPointsRequest(String name,
                                                    PartnerType partnerType,
                                                    PartnerPointRequest[] points) {
    private record PartnerPointRequest(String name, PartnerPointType partnerPointType) {
    }
}
