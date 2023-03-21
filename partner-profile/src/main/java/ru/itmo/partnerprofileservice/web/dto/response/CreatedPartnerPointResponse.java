package ru.itmo.partnerprofileservice.web.dto.response;

import ru.itmo.partnerprofileservice.domain.enumeration.PartnerPointType;

public record CreatedPartnerPointResponse(String partnerPointId,
                                          String partnerProfileId,
                                          String name,
                                          PartnerPointType partnerPointType) {
}
