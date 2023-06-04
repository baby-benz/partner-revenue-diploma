package ru.itmo.profilepointservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.HttpErrorCause;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.client.CalcSchemeClient;
import ru.itmo.profilepointservice.domain.entity.Point;
import ru.itmo.profilepointservice.domain.enumeration.Status;
import ru.itmo.profilepointservice.repository.PointRepository;
import ru.itmo.profilepointservice.repository.ProfileRepository;
import ru.itmo.profilepointservice.service.PointService;
import ru.itmo.profilepointservice.service.so.in.CreatePointSO;
import ru.itmo.profilepointservice.service.so.in.UpdatePointSO;
import ru.itmo.profilepointservice.service.so.out.CreatedPointSO;
import ru.itmo.profilepointservice.service.so.out.FullPointSO;
import ru.itmo.profilepointservice.service.so.out.RequestedFieldsPointSO;
import ru.itmo.profilepointservice.service.so.out.UpdatedPointSO;
import ru.itmo.profilepointservice.web.dto.request.ResponsePointFields;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultPointService implements PointService {
    private final ProfileRepository profileRepository;
    private final PointRepository pointRepository;
    private final CalcSchemeClient calcSchemeClient;

    @Override
    public CreatedPointSO createPoint(CreatePointSO pointData) {
        checkProfileExistence(pointData.profileId());

        var pointToSave = new Point(
                UUID.randomUUID(),
                pointData.name(),
                profileRepository.getReferenceById(UUID.fromString(pointData.profileId())),
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
                createdPoint.getId().toString(),
                pointData.profileId(),
                createdPoint.getName(),
                createdPoint.getPointType(),
                createdPoint.getStatus(),
                pointData.calcSchemeId()
        );
    }

    @Override
    public FullPointSO getPoint(String pointId) {
        checkExistence(pointId);

        Point point = pointRepository.getReferenceById(UUID.fromString(pointId));

        return new FullPointSO(
                pointId,
                point.getProfile().getId().toString(),
                point.getName(),
                point.getPointType(),
                point.getStatus(),
                point.getCalcSchemeId() == null ? null : point.getCalcSchemeId().toString()
        );
    }

    @Override
    public UpdatedPointSO updatePoint(UpdatePointSO pointData) {
        checkExistence(pointData.pointId());

        UUID pointId = UUID.fromString(pointData.pointId());
        UUID profileId = UUID.fromString(pointData.profileId());

        Point pointToUpdate = pointRepository.getReferenceById(pointId);

        if (!profileId.equals(pointToUpdate.getProfile().getId())) {
            checkProfileExistence(pointData.profileId());
        }

        UUID calcSchemeId = null;

        if (pointData.calcSchemeId() != null) {
            calcSchemeId = UUID.fromString(pointData.calcSchemeId());
            if (pointToUpdate.getCalcSchemeId() != null && !pointToUpdate.getCalcSchemeId().equals(UUID.fromString(pointData.calcSchemeId()))) {
                calcSchemeClient.checkCalcSchemeExistence(pointData.calcSchemeId());
            }
        }

        Point updatedPoint = pointRepository.save(
                new Point(
                        pointId,
                        pointData.name(),
                        profileRepository.getReferenceById(profileId),
                        pointData.pointType(),
                        pointData.status(),
                        calcSchemeId
                )
        );

        return new UpdatedPointSO(
                pointData.pointId(),
                pointData.profileId(),
                updatedPoint.getName(),
                updatedPoint.getPointType(),
                updatedPoint.getStatus(),
                updatedPoint.getCalcSchemeId() == null ? null : updatedPoint.getCalcSchemeId().toString()
        );
    }

    @Override
    public boolean pointMatchesProfile(String pointId, String profileId) {
        checkExistence(pointId);
        checkProfileExistence(profileId);
        Point point = new Point();
        point.setId(UUID.fromString(pointId));
        point.setProfile(profileRepository.getReferenceById(UUID.fromString(profileId)));
        return pointRepository.exists(Example.of(point));
    }

    @Override
    public UpdatedPointSO setCalcScheme(String pointId, String calcSchemeId) {
        checkExistence(pointId);

        Point pointToSetCalcScheme = pointRepository.getReferenceById(UUID.fromString(pointId));

        UUID calcSchemeUuid = UUID.fromString(calcSchemeId);

        if (pointToSetCalcScheme.getCalcSchemeId() == null
                || !pointToSetCalcScheme.getCalcSchemeId().equals(calcSchemeUuid)) {
            log.debug("Checking calc scheme with id " + calcSchemeId + " existence");
            calcSchemeClient.checkCalcSchemeExistence(calcSchemeId);
            log.debug("Calc scheme with id " + calcSchemeId + " was successfully found");
        }

        pointToSetCalcScheme.setCalcSchemeId(calcSchemeUuid);

        Point updatedPoint = pointRepository.save(pointToSetCalcScheme);

        return new UpdatedPointSO(
                pointId,
                updatedPoint.getProfile().getId().toString(),
                updatedPoint.getName(),
                updatedPoint.getPointType(),
                updatedPoint.getStatus(),
                calcSchemeId
        );
    }

    @Override
    public UpdatedPointSO updateStatus(String pointId, Status status) {
        checkExistence(pointId);

        Point pointToSetCalcScheme = pointRepository.getReferenceById(UUID.fromString(pointId));

        pointToSetCalcScheme.setStatus(status);

        Point updatedPoint = pointRepository.save(pointToSetCalcScheme);

        return new UpdatedPointSO(
                pointId,
                updatedPoint.getProfile().getId().toString(),
                updatedPoint.getName(),
                updatedPoint.getPointType(),
                updatedPoint.getStatus(),
                updatedPoint.getCalcSchemeId() == null ? null : updatedPoint.getCalcSchemeId().toString()
        );
    }

    @Override
    public RequestedFieldsPointSO retrieveRequestedSetOfFields(String pointId, ResponsePointFields[] fieldsList) {
        checkExistence(pointId);

        Point point = pointRepository.getReferenceById(UUID.fromString(pointId));

        if (fieldsList == null || fieldsList.length == 0) {
            return new RequestedFieldsPointSO(
                    pointId,
                    point.getProfile().getId().toString(),
                    point.getName(),
                    point.getPointType(),
                    point.getStatus(),
                    point.getCalcSchemeId() == null ? null : point.getCalcSchemeId().toString()
            );
        }

        RequestedFieldsPointSO.RequestedFieldsPointSOBuilder responseBuilder = RequestedFieldsPointSO.builder();

        responseBuilder.pointId(pointId);

        for (var fieldToReturn : fieldsList) {
            switch (fieldToReturn) {
                case PROFILE_ID -> responseBuilder.profileId(point.getProfile().getId().toString());
                case NAME -> responseBuilder.name(point.getName());
                case POINT_TYPE -> responseBuilder.pointType(point.getPointType());
                case STATUS -> responseBuilder.status(point.getStatus());
                case CALC_SCHEME_ID ->
                        responseBuilder.calcSchemeId(point.getCalcSchemeId() == null ? null : point.getCalcSchemeId().toString());
            }
        }

        return responseBuilder.build();
    }

    @Override
    public List<String> getPointIdsByProfileId(String profileId) {
        checkProfileExistence(profileId);

        return pointRepository.findAllByProfile_Id(
                UUID.fromString(profileId)
        ).stream().map(point -> point.getId().toString()).toList();
    }

    private void checkProfileExistence(String profileId) {
        if (!profileRepository.existsById(UUID.fromString(profileId))) {
            log.debug("Profile with id " + profileId + " was not found");
            throw new HttpStatusCodeException(
                    new NotFoundErrorCause(List.of(HttpErrorCause.NotFound.PROFILE_NOT_FOUND)),
                    profileId
            );
        }
        log.debug("Profile with id " + profileId + " was successfully found");
    }

    private void checkExistence(String pointId) {
        if (!pointRepository.existsById(UUID.fromString(pointId))) {
            log.debug("Point with id " + pointId + " was not found");
            throw new HttpStatusCodeException(
                    new NotFoundErrorCause(List.of(HttpErrorCause.NotFound.POINT_NOT_FOUND)),
                    pointId
            );
        }
        log.debug("Point with id " + pointId + " was successfully found");
    }
}
