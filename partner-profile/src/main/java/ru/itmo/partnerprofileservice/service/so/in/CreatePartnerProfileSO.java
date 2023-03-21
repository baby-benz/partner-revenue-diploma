package ru.itmo.partnerprofileservice.service.so.in;

import ru.itmo.partnerprofileservice.domain.enumeration.PartnerType;

public record CreatePartnerProfileSO(String name, PartnerType partnerType) {
}
