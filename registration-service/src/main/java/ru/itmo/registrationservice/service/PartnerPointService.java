package ru.itmo.registrationservice.service;

import ru.itmo.registrationservice.service.so.in.CreatePartnerPointSO;
import ru.itmo.registrationservice.service.so.out.CreatedPartnerPointSO;

public interface PartnerPointService {
    CreatedPartnerPointSO createPartnerPoint(CreatePartnerPointSO partnerPointData);
}
