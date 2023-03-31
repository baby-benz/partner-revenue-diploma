package ru.itmo.point.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
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

    @Override
    public CreatedPointSO createPoint(CreatePointSO pointData) {
        profileClient.checkProfileExistence(pointData.profileId());

        Point createdPoint = pointRepository.save(
                new Point(
                        UUID.randomUUID().toString(),
                        pointData.name(),
                        pointData.profileId(),
                        pointData.pointType(),
                        pointData.status()
                )
        );

        return new CreatedPointSO(
                createdPoint.getId(),
                createdPoint.getProfileId(),
                createdPoint.getName(),
                createdPoint.getPointType(),
                createdPoint.getStatus()
        );
    }

    @Override
    public FullPointSO getPoint(String pointId) {
        if (!pointRepository.existsById(pointId)) {
            throw new HttpStatusCodeException(NotFoundErrorCause.POINT_NOT_FOUND, pointId);
        }

        Point point = pointRepository.getReferenceById(pointId);

        return new FullPointSO(
                point.getId(),
                point.getProfileId(),
                point.getName(),
                point.getPointType(),
                point.getStatus()
        );
    }

    @Override
    public UpdatedPointSO updatePoint(UpdatePointSO pointData) {
        if (!pointRepository.existsById(pointData.pointId())) {
            throw new HttpStatusCodeException(NotFoundErrorCause.POINT_NOT_FOUND, pointData.pointId());
        }

        profileClient.checkProfileExistence(pointData.profileId());

        Point updatedPoint = pointRepository.save(
                new Point(
                        pointData.pointId(),
                        pointData.name(),
                        pointData.profileId(),
                        pointData.pointType(),
                        pointData.status()
                )
        );

        return new UpdatedPointSO(
                updatedPoint.getId(),
                updatedPoint.getProfileId(),
                updatedPoint.getName(),
                updatedPoint.getPointType(),
                updatedPoint.getStatus()
        );
    }

    @Override
    public void checkPointAndProfileMatch(String pointId, String profileId) {
        Point point = new Point();
        point.setId(pointId);
        point.setProfileId(profileId);
        if (!pointRepository.exists(Example.of(point))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
