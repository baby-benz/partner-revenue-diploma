package ru.itmo.partnerprofileservice.web.dto.response;

import ru.itmo.partnerprofileservice.domain.enumeration.PartnerType;

public record CreatedPartnerProfileResponse(String partnerProfileId, String name, PartnerType partnerType) {
}
