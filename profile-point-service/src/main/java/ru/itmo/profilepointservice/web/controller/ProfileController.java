package ru.itmo.profilepointservice.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.profilepointservice.service.ProfileService;
import ru.itmo.profilepointservice.service.so.in.CreateProfileSO;
import ru.itmo.profilepointservice.service.so.in.UpdateProfileSO;
import ru.itmo.profilepointservice.service.so.out.CreatedProfileSO;
import ru.itmo.profilepointservice.service.so.out.FullProfileSO;
import ru.itmo.profilepointservice.service.so.out.UpdatedProfileSO;
import ru.itmo.profilepointservice.web.dto.request.CreateProfileRequest;
import ru.itmo.profilepointservice.web.dto.request.UpdateProfileRequest;
import ru.itmo.profilepointservice.web.dto.response.CreatedProfileResponse;
import ru.itmo.profilepointservice.web.dto.response.FullProfileResponse;
import ru.itmo.profilepointservice.web.dto.response.UpdatedProfileResponse;
import ru.itmo.common.web.validation.ValidUUID;

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

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = Endpoint.Profile.GET_FULL, produces = MediaType.APPLICATION_JSON_VALUE)
    public FullProfileResponse getFullProfile(@ValidUUID @PathVariable String profileId) {
        FullProfileSO profile = profileService.getProfile(profileId);

        return new FullProfileResponse(
                profile.profileId(),
                profile.name(),
                profile.profileType(),
                profile.status()
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = Endpoint.Profile.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UpdatedProfileResponse updateProfile(@ValidUUID @PathVariable String profileId, @Valid @RequestBody UpdateProfileRequest profileRequestBody) {
        UpdatedProfileSO updatedProfile = profileService.updateProfile(
                new UpdateProfileSO(
                        profileId,
                        profileRequestBody.name(),
                        profileRequestBody.profileType(),
                        profileRequestBody.status()
                )
        );

        return new UpdatedProfileResponse(
                updatedProfile.profileId(),
                updatedProfile.name(),
                updatedProfile.profileType(),
                updatedProfile.status()
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = Endpoint.Profile.PATCH_SUSPEND, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void suspendProfile(@ValidUUID @PathVariable String profileId) {
        profileService.suspendProfile(profileId);
    }
}
