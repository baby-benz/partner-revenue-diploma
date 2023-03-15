package ru.itmo.registrationservice.web.dto.request;

import ru.itmo.registrationservice.domain.enumeration.PartnerType;

public record CreatePartnerProfileRequest(String name, PartnerType partnerType) {
}
