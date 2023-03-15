package ru.itmo.registrationservice.service.so.in;

import ru.itmo.registrationservice.domain.enumeration.PartnerPointType;

public record CreatePartnerPointSO(String partnerProfileId, String name, PartnerPointType partnerPointType) {
}
