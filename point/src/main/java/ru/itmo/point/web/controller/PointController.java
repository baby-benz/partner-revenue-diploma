package ru.itmo.point.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.point.service.PointService;
import ru.itmo.point.service.so.in.CreatePointSO;
import ru.itmo.point.service.so.in.UpdatePointSO;
import ru.itmo.point.service.so.out.CreatedPointSO;
import ru.itmo.point.service.so.out.FullPointSO;
import ru.itmo.point.service.so.out.UpdatedPointSO;
import ru.itmo.point.web.dto.request.CreatePointRequest;
import ru.itmo.point.web.dto.request.UpdatePointRequest;
import ru.itmo.point.web.dto.response.CreatedPointResponse;
import ru.itmo.point.web.dto.response.FullPointResponse;
import ru.itmo.point.web.dto.response.UpdatedPointResponse;

@RequiredArgsConstructor
@RestController
public class PointController {
    private final PointService pointService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = Endpoint.Point.POST_NEW, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CreatedPointResponse addPoint(@RequestBody CreatePointRequest pointRequestBody) {
        CreatedPointSO createdPoint = pointService.createPoint(
                new CreatePointSO(
                        pointRequestBody.profileId(),
                        pointRequestBody.name(),
                        pointRequestBody.pointType(),
                        pointRequestBody.status()
                )
        );

        return new CreatedPointResponse(
                createdPoint.pointId(),
                createdPoint.profileId(),
                createdPoint.name(),
                createdPoint.pointType(),
                createdPoint.status()
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = Endpoint.Point.GET_FULL, produces = MediaType.APPLICATION_JSON_VALUE)
    public FullPointResponse getFullPoint(@PathVariable String pointId) {
        FullPointSO point = pointService.getPoint(pointId);

        return new FullPointResponse(
                point.pointId(),
                point.profileId(),
                point.name(),
                point.pointType(),
                point.status()
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = Endpoint.Point.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UpdatedPointResponse updatePoint(@PathVariable String pointId, @RequestBody UpdatePointRequest pointRequestBody) {
        UpdatedPointSO updatedPoint = pointService.updatePoint(
                new UpdatePointSO(
                        pointId,
                        pointRequestBody.profileId(),
                        pointRequestBody.name(),
                        pointRequestBody.pointType(),
                        pointRequestBody.status()
                )
        );

        return new UpdatedPointResponse(
                updatedPoint.pointId(),
                updatedPoint.profileId(),
                updatedPoint.name(),
                updatedPoint.pointType(),
                updatedPoint.status()
        );
    }
}
