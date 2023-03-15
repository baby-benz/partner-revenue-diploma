package ru.itmo.registrationservice.web.dto.request;

import ru.itmo.registrationservice.domain.enumeration.PartnerPointType;

public record CreatePartnerPointRequest(String partnerProfileId, String name, PartnerPointType partnerPointType) {
}
