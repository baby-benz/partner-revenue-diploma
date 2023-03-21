package ru.itmo.partnerprofileservice.service;

import ru.itmo.partnerprofileservice.service.so.in.CreatePartnerProfileSO;
import ru.itmo.partnerprofileservice.service.so.out.CreatedPartnerProfileSO;

public interface PartnerProfileService {
    CreatedPartnerProfileSO createPartnerProfile(CreatePartnerProfileSO partnerProfileData);
}
