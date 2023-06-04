package ru.itmo.profilepointservice.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.profilepointservice.domain.enumeration.Status;
import ru.itmo.profilepointservice.service.PointService;
import ru.itmo.profilepointservice.service.so.in.CreatePointSO;
import ru.itmo.profilepointservice.service.so.in.UpdatePointSO;
import ru.itmo.profilepointservice.service.so.out.CreatedPointSO;
import ru.itmo.profilepointservice.service.so.out.FullPointSO;
import ru.itmo.profilepointservice.service.so.out.UpdatedPointSO;
import ru.itmo.profilepointservice.web.dto.request.CreatePointRequest;
import ru.itmo.profilepointservice.web.dto.request.UpdatePointRequest;
import ru.itmo.profilepointservice.web.dto.response.CreatedPointResponse;
import ru.itmo.profilepointservice.web.dto.response.FullPointResponse;
import ru.itmo.profilepointservice.web.dto.response.UpdatedPointResponse;
import ru.itmo.common.web.validation.ValidUUID;

@Validated
@RequiredArgsConstructor
@RestController
public class PointController {
    private final PointService pointService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = Endpoint.Point.POST_NEW, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CreatedPointResponse addPoint(@Valid @RequestBody CreatePointRequest pointRequestBody) {
        CreatedPointSO createdPoint = pointService.createPoint(
                new CreatePointSO(
                        pointRequestBody.profileId(),
                        pointRequestBody.name(),
                        pointRequestBody.pointType(),
                        pointRequestBody.status(),
                        pointRequestBody.calcSchemeId()
                )
        );

        return new CreatedPointResponse(
                createdPoint.pointId(),
                createdPoint.profileId(),
                createdPoint.name(),
                createdPoint.pointType(),
                createdPoint.status(),
                createdPoint.calcSchemeId()
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = Endpoint.Point.GET_FULL, produces = MediaType.APPLICATION_JSON_VALUE)
    public FullPointResponse getFullPoint(@NotBlank @PathVariable String pointId) {
        FullPointSO point = pointService.getPoint(pointId);

        return new FullPointResponse(
                point.pointId(),
                point.profileId(),
                point.name(),
                point.pointType(),
                point.status(),
                point.calcSchemeId()
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = Endpoint.Point.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UpdatedPointResponse updatePoint(@ValidUUID @PathVariable String pointId, @Valid @RequestBody UpdatePointRequest pointRequestBody) {
        UpdatedPointSO updatedPoint = pointService.updatePoint(
                new UpdatePointSO(
                        pointId,
                        pointRequestBody.profileId(),
                        pointRequestBody.name(),
                        pointRequestBody.pointType(),
                        pointRequestBody.status(),
                        pointRequestBody.calcSchemeId()
                )
        );

        return new UpdatedPointResponse(
                updatedPoint.pointId(),
                updatedPoint.profileId(),
                updatedPoint.name(),
                updatedPoint.pointType(),
                updatedPoint.status(),
                updatedPoint.calcSchemeId()
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = Endpoint.Point.PATCH_CALC_SCHEME, produces = MediaType.APPLICATION_JSON_VALUE)
    public UpdatedPointResponse setCalcScheme(@NotBlank @PathVariable String pointId, @NotBlank @RequestParam String calcSchemeId) {
        UpdatedPointSO updatedPoint = pointService.setCalcScheme(pointId, calcSchemeId);

        return new UpdatedPointResponse(
                updatedPoint.pointId(),
                updatedPoint.profileId(),
                updatedPoint.name(),
                updatedPoint.pointType(),
                updatedPoint.status(),
                updatedPoint.calcSchemeId()
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = Endpoint.Point.PATCH_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    public UpdatedPointResponse updateStatus(@NotBlank @PathVariable String pointId, @NotNull @RequestParam Status status) {
        UpdatedPointSO updatedPoint = pointService.updateStatus(pointId, status);

        return new UpdatedPointResponse(
                updatedPoint.pointId(),
                updatedPoint.profileId(),
                updatedPoint.name(),
                updatedPoint.pointType(),
                updatedPoint.status(),
                updatedPoint.calcSchemeId()
        );
    }
}
