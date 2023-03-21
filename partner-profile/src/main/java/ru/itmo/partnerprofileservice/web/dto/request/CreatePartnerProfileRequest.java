package ru.itmo.partnerprofileservice.web.dto.request;

import ru.itmo.partnerprofileservice.domain.enumeration.PartnerType;

public record CreatePartnerProfileRequest(String name, PartnerType partnerType) {
}
