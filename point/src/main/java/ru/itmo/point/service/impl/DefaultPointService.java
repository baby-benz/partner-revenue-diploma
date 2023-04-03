package ru.itmo.point.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.client.CalcSchemeClient;
import ru.itmo.common.web.client.ProfileClient;
import ru.itmo.point.domain.entity.Point;
import ru.itmo.point.repository.PointRepository;
import ru.itmo.point.service.PointService;
import ru.itmo.point.service.so.in.CreatePointSO;
import ru.itmo.point.service.so.in.UpdatePointSO;
import ru.itmo.point.service.so.out.CreatedPointSO;
import ru.itmo.point.service.so.out.FullPointSO;
import ru.itmo.point.service.so.out.UpdatedPointSO;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultPointService implements PointService {
    private final PointRepository pointRepository;
    private final ProfileClient profileClient;
    private final CalcSchemeClient calcSchemeClient;

    @Override
    public CreatedPointSO createPoint(CreatePointSO pointData) {
        profileClient.checkProfileExistence(pointData.profileId());

        var pointToSave = new Point(
                UUID.randomUUID().toString(),
                pointData.name(),
                pointData.profileId(),
                pointData.pointType()
        );

        if (pointData.status() != null) {
            pointToSave.setStatus(pointData.status());
        }

        if (pointData.calcSchemeId() != null) {
            calcSchemeClient.checkCalcSchemeExistence(pointData.calcSchemeId());
        }

        Point createdPoint = pointRepository.save(pointToSave);

        return new CreatedPointSO(
                createdPoint.getId(),
                createdPoint.getProfileId(),
                createdPoint.getName(),
                createdPoint.getPointType(),
                createdPoint.getStatus(),
                createdPoint.getCalcSchemeId()
        );
    }

    @Override
    public FullPointSO getPoint(String pointId) {
        Point point;

        try {
            point = pointRepository.getReferenceById(pointId);
            log.debug("Point to get with id " + pointId + " was successfully found");
        } catch (EntityNotFoundException e) {
            log.debug("Point to get with id " + pointId + " was not found");
            throw new HttpStatusCodeException(NotFoundErrorCause.POINT_NOT_FOUND, pointId);
        }

        return new FullPointSO(
                point.getId(),
                point.getProfileId(),
                point.getName(),
                point.getPointType(),
                point.getStatus(),
                point.getCalcSchemeId()
        );
    }

    @Override
    public UpdatedPointSO updatePoint(UpdatePointSO pointData) {
        try {
            Point pointToUpdate = pointRepository.getReferenceById(pointData.pointId());
            log.debug("Point to update with id " + pointData.pointId() + " was successfully found");

            if (!pointData.profileId().equals(pointToUpdate.getProfileId())) {
                profileClient.checkProfileExistence(pointData.profileId());
            }

            if ((pointToUpdate.getCalcSchemeId() != null && !pointToUpdate.getCalcSchemeId().equals(pointData.calcSchemeId()))
                    || pointData.calcSchemeId() != null) {
                calcSchemeClient.checkCalcSchemeExistence(pointData.calcSchemeId());
            }
        } catch (EntityNotFoundException e) {
            log.debug("Point to update with id " + pointData.pointId() + " was not found");
            throw new HttpStatusCodeException(NotFoundErrorCause.POINT_NOT_FOUND, pointData.pointId());
        }

        Point updatedPoint = pointRepository.save(
                new Point(
                        pointData.pointId(),
                        pointData.name(),
                        pointData.profileId(),
                        pointData.pointType(),
                        pointData.status(),
                        pointData.calcSchemeId()
                )
        );

        return new UpdatedPointSO(
                updatedPoint.getId(),
                updatedPoint.getProfileId(),
                updatedPoint.getName(),
                updatedPoint.getPointType(),
                updatedPoint.getStatus(),
                updatedPoint.getCalcSchemeId()
        );
    }

    @Override
    public boolean pointMatchesProfile(String pointId, String profileId) {
        Point point = new Point();
        point.setId(pointId);
        point.setProfileId(profileId);
        return pointRepository.exists(Example.of(point));
    }

    @Override
    public UpdatedPointSO setCalcScheme(String pointId, String calcSchemeId) {
        Point pointToSetCalcScheme;

        try {
            pointToSetCalcScheme = pointRepository.getReferenceById(pointId);
            log.debug("Point to set calc scheme with id " + pointId + " was successfully found");

            if (pointToSetCalcScheme.getCalcSchemeId() == null
                    || !pointToSetCalcScheme.getCalcSchemeId().equals(calcSchemeId)) {
                log.debug("Checking calc scheme with id " + calcSchemeId + " existence");
                calcSchemeClient.checkCalcSchemeExistence(calcSchemeId);
            }
        } catch (EntityNotFoundException e) {
            log.debug("Point to set calc scheme with id " + pointId + " was not found");
            throw new HttpStatusCodeException(NotFoundErrorCause.POINT_NOT_FOUND, pointId);
        }

        pointToSetCalcScheme.setCalcSchemeId(calcSchemeId);

        Point updatedPoint = pointRepository.save(pointToSetCalcScheme);

        return new UpdatedPointSO(
                updatedPoint.getId(),
                updatedPoint.getProfileId(),
                updatedPoint.getName(),
                updatedPoint.getPointType(),
                updatedPoint.getStatus(),
                updatedPoint.getCalcSchemeId()
        );
    }
}
