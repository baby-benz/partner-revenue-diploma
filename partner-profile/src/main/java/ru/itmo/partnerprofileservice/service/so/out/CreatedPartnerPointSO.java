package ru.itmo.partnerprofileservice.service.so.out;

import ru.itmo.partnerprofileservice.domain.enumeration.PartnerPointType;

public record CreatedPartnerPointSO(String partnerPointId,
                                    String partnerProfileId,
                                    String name,
                                    PartnerPointType partnerPointType) {
}
