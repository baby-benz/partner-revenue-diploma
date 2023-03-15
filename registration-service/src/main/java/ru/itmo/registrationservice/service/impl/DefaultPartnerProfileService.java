package ru.itmo.registrationservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.registrationservice.domain.entity.Partner;
import ru.itmo.registrationservice.repository.PartnerProfileRepository;
import ru.itmo.registrationservice.service.PartnerProfileService;
import ru.itmo.registrationservice.service.so.in.CreatePartnerProfileSO;
import ru.itmo.registrationservice.service.so.out.CreatedPartnerProfileSO;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DefaultPartnerProfileService implements PartnerProfileService {
    private final PartnerProfileRepository partnerProfileRepository;

    @Override
    public CreatedPartnerProfileSO createPartnerProfile(CreatePartnerProfileSO partnerProfileData) {
        Partner createdPartnerProfile = partnerProfileRepository.save(
                new Partner(
                        UUID.randomUUID().toString(),
                        partnerProfileData.name(),
                        partnerProfileData.partnerType()
                )
        );
        return new CreatedPartnerProfileSO(
                createdPartnerProfile.getId(),
                createdPartnerProfile.getName(),
                createdPartnerProfile.getPartnerType()
        );
    }
}
