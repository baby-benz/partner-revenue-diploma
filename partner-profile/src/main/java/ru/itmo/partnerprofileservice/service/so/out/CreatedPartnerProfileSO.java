package ru.itmo.partnerprofileservice.service.so.out;

import ru.itmo.partnerprofileservice.domain.enumeration.PartnerType;

public record CreatedPartnerProfileSO(String partnerProfileId, String name, PartnerType partnerType) {
}
