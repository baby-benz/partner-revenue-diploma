package ru.itmo.registrationservice.service;

import ru.itmo.registrationservice.service.so.in.CreatePartnerProfileSO;
import ru.itmo.registrationservice.service.so.out.CreatedPartnerProfileSO;

public interface PartnerProfileService {
    CreatedPartnerProfileSO createPartnerProfile(CreatePartnerProfileSO partnerProfileData);
}
