package ru.itmo.partnerprofileservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.partnerprofileservice.domain.entity.Point;
import ru.itmo.partnerprofileservice.domain.entity.Partner;
import ru.itmo.partnerprofileservice.repository.PartnerPointRepository;
import ru.itmo.partnerprofileservice.repository.PartnerProfileRepository;
import ru.itmo.partnerprofileservice.service.PartnerPointService;
import ru.itmo.partnerprofileservice.service.so.in.CreatePartnerPointSO;
import ru.itmo.partnerprofileservice.service.so.out.CreatedPartnerPointSO;

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
