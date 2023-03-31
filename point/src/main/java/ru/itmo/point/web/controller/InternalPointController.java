package ru.itmo.point.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.itmo.point.constant.InternalEndpoint;
import ru.itmo.point.service.PointService;

@RequiredArgsConstructor
@RestController
public class InternalPointController {
    private final PointService pointService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = InternalEndpoint.HEAD_CHECK_POINT)
    public void checkPointAndProfileMatch(@PathVariable String pointId, @RequestParam String profileId) {
        pointService.checkPointAndProfileMatch(pointId, profileId);
    }
}
