package ru.itmo.registrationservice.web.dto.response;

import ru.itmo.registrationservice.domain.enumeration.PartnerPointType;

public record CreatedPartnerPointResponse(String partnerPointId,
                                          String partnerProfileId,
                                          String name,
                                          PartnerPointType partnerPointType) {
}
