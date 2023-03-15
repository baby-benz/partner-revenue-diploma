package ru.itmo.registrationservice.web.dto.response;

import ru.itmo.registrationservice.domain.enumeration.PartnerType;

public record CreatedPartnerProfileResponse(String partnerProfileId, String name, PartnerType partnerType) {
}
