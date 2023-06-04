package ru.itmo.profilepointservice.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.common.constant.InternalEndpoint;
import ru.itmo.profilepointservice.service.PointService;
import ru.itmo.profilepointservice.web.dto.request.ResponsePointFields;
import ru.itmo.common.web.validation.ValidUUID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RequiredArgsConstructor
@RestController
public class InternalPointController {
    private final PointService pointService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = InternalEndpoint.Point.HEAD_CHECK_POINT)
    public void checkPointAndProfileMatching(@ValidUUID @PathVariable String pointId, @ValidUUID @RequestParam String profileId) {
        if (!pointService.pointMatchesProfile(pointId, profileId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = InternalEndpoint.Point.GET_REQUESTED_POINT_FIELDS)
    public Map<String, Object> getRequestedPointFields(@ValidUUID @PathVariable String pointId, @RequestParam ResponsePointFields[] fieldsToInclude) {
        var response = pointService.retrieveRequestedSetOfFields(pointId, fieldsToInclude);

        if (fieldsToInclude != null) {
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("pointId", response.pointId());
            for (var field : fieldsToInclude) {
                switch (field) {
                    case PROFILE_ID -> responseMap.put("profileId", response.profileId());
                    case NAME -> responseMap.put("name", response.name());
                    case POINT_TYPE -> responseMap.put("pointType", response.pointType());
                    case STATUS -> responseMap.put("status", response.status());
                    case CALC_SCHEME_ID -> responseMap.put("calcSchemeId", response.calcSchemeId());
                }
            }
            return responseMap;
        } else {
            return Map.of(
                    "pointId", response.pointId(),
                    "profileId", response.profileId(),
                    "pointType", response.pointType(),
                    "status", response.status(),
                    "calcSchemeId", response.calcSchemeId()
            );
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = InternalEndpoint.Point.GET_POINT_IDS_WITH_PROFILE_ID)
    public List<String> getPointIdsWithProfileId(@ValidUUID @RequestParam String profileId) {
        return pointService.getPointIdsByProfileId(profileId);
    }
}
