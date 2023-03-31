package ru.itmo.profile.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.itmo.profile.constant.InternalEndpoint;
import ru.itmo.profile.service.ProfileService;

@RequiredArgsConstructor
@RestController
public class InternalProfileController {
    private final ProfileService profileService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = InternalEndpoint.HEAD_CHECK_PROFILE)
    public void checkProfile(@PathVariable String profileId) {
        profileService.checkProfile(profileId);
    }
}
