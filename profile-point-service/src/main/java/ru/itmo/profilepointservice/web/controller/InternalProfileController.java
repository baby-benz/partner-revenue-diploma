package ru.itmo.profilepointservice.web.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.common.constant.InternalEndpoint;
import ru.itmo.profilepointservice.service.ProfileService;

import java.util.Map;

@Validated
@RequiredArgsConstructor
@RestController
public class InternalProfileController {
    private final ProfileService profileService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = InternalEndpoint.Profile.HEAD_CHECK_PROFILE)
    public void checkProfileExistence(@NotBlank @PathVariable String profileId) {
        if (!profileService.profileExists(profileId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
