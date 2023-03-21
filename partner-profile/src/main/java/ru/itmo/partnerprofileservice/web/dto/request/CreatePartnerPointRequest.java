package ru.itmo.partnerprofileservice.web.dto.request;

import ru.itmo.partnerprofileservice.domain.enumeration.PartnerPointType;

public record CreatePartnerPointRequest(String partnerProfileId, String name, PartnerPointType partnerPointType) {
}
