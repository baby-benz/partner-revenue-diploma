package ru.itmo.partnerprofileservice.service;

import ru.itmo.partnerprofileservice.service.so.in.CreatePartnerPointSO;
import ru.itmo.partnerprofileservice.service.so.out.CreatedPartnerPointSO;

public interface PartnerPointService {
    CreatedPartnerPointSO createPartnerPoint(CreatePartnerPointSO partnerPointData);
}
