package ru.itmo.registrationservice.service.so.out;

import ru.itmo.registrationservice.domain.enumeration.PartnerType;

public record CreatedPartnerProfileSO(String partnerProfileId, String name, PartnerType partnerType) {
}
