package ru.itmo.registrationservice.service.so.out;

import ru.itmo.registrationservice.domain.enumeration.PartnerPointType;

public record CreatedPartnerPointSO(String partnerPointId,
                                    String partnerProfileId,
                                    String name,
                                    PartnerPointType partnerPointType) {
}
