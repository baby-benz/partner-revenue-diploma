package ru.itmo.registrationservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.registrationservice.domain.entity.Point;
import ru.itmo.registrationservice.domain.entity.Partner;
import ru.itmo.registrationservice.repository.PartnerPointRepository;
import ru.itmo.registrationservice.repository.PartnerProfileRepository;
import ru.itmo.registrationservice.service.PartnerPointService;
import ru.itmo.registrationservice.service.so.in.CreatePartnerPointSO;
import ru.itmo.registrationservice.service.so.out.CreatedPartnerPointSO;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DefaultPartnerPointService implements PartnerPointService {
    private final PartnerPointRepository partnerPointRepository;
    private final PartnerProfileRepository partnerProfileRepository;

    @Override
    public CreatedPartnerPointSO createPartnerPoint(CreatePartnerPointSO partnerPointData) {
        if (!partnerProfileRepository.existsById(partnerPointData.partnerProfileId())) {
            throw new HttpStatusCodeException(NotFoundErrorCause.PARTNER_PROFILE_NOT_FOUND, partnerPointData.partnerProfileId());
        }

        Point createdPartnerPoint = partnerPointRepository.save(
                new Point(
                        UUID.randomUUID().toString(),
                        partnerPointData.name(),
                        new Partner(partnerPointData.partnerProfileId()),
                        partnerPointData.partnerPointType()
                )
        );
        return new CreatedPartnerPointSO(
                createdPartnerPoint.getId(),
                createdPartnerPoint.getPartnerProfile().getId(),
                createdPartnerPoint.getName(),
                createdPartnerPoint.getPartnerPointType()
        );
    }
}
