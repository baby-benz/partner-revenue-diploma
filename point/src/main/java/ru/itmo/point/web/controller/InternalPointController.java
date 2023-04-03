package ru.itmo.point.web.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.point.constant.InternalEndpoint;
import ru.itmo.point.service.PointService;

@Validated
@RequiredArgsConstructor
@RestController
public class InternalPointController {
    private final PointService pointService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = InternalEndpoint.HEAD_CHECK_POINT)
    public void checkPointAndProfileMatching(@NotBlank @PathVariable String pointId, @NotBlank @RequestParam String profileId) {
        if (!pointService.pointMatchesProfile(pointId, profileId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
