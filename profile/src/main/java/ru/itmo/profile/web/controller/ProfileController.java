package ru.itmo.profile.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.profile.service.ProfileService;
import ru.itmo.profile.service.so.in.CreateProfileSO;
import ru.itmo.profile.service.so.out.CreatedProfileSO;
import ru.itmo.profile.web.dto.request.CreateProfileRequest;
import ru.itmo.profile.web.dto.response.CreatedProfileResponse;

@RequiredArgsConstructor
@RestController
public class ProfileController {
    private final ProfileService profileService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = Endpoint.Profile.POST_NEW, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CreatedProfileResponse createProfile(@Valid @RequestBody CreateProfileRequest profileRequest) {
        CreatedProfileSO createdProfile = profileService.createProfile(
                new CreateProfileSO(
                        profileRequest.name(),
                        profileRequest.profileType(),
                        profileRequest.status()
                )
        );

        return new CreatedProfileResponse(
                createdProfile.profileId(),
                createdProfile.name(),
                createdProfile.profileType(),
                createdProfile.status()
        );
    }
}
