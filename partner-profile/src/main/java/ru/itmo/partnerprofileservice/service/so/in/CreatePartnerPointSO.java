package ru.itmo.partnerprofileservice.service.so.in;

import ru.itmo.partnerprofileservice.domain.enumeration.PartnerPointType;

public record CreatePartnerPointSO(String partnerProfileId, String name, PartnerPointType partnerPointType) {
}
