package ru.itmo.registrationservice.service.so.in;

import ru.itmo.registrationservice.domain.enumeration.PartnerType;

public record CreatePartnerProfileSO(String name, PartnerType partnerType) {
}
