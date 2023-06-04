package ru.itmo.profilepointservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.HttpErrorCause;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.profilepointservice.domain.entity.Profile;
import ru.itmo.profilepointservice.domain.enumeration.Status;
import ru.itmo.profilepointservice.repository.ProfileRepository;
import ru.itmo.profilepointservice.service.ProfileService;
import ru.itmo.profilepointservice.service.so.in.CreateProfileSO;
import ru.itmo.profilepointservice.service.so.in.UpdateProfileSO;
import ru.itmo.profilepointservice.service.so.out.CreatedProfileSO;
import ru.itmo.profilepointservice.service.so.out.FullProfileSO;
import ru.itmo.profilepointservice.service.so.out.UpdatedProfileSO;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultProfileService implements ProfileService {
    private final ProfileRepository profileRepository;

    @Override
    public CreatedProfileSO createProfile(CreateProfileSO profileData) {
        var profileToCreate = new Profile(UUID.randomUUID(), profileData.name(), profileData.profileType());

        if (profileData.status() != null) {
            profileToCreate.setStatus(profileData.status());
        }

        Profile createdProfile = profileRepository.save(profileToCreate);

        return new CreatedProfileSO(
                createdProfile.getId().toString(),
                createdProfile.getName(),
                createdProfile.getProfileType(),
                createdProfile.getStatus()
        );
    }

    @Override
    public FullProfileSO getProfile(String profileId) {
        checkExistence(profileId);

        Profile profile = profileRepository.getReferenceById(UUID.fromString(profileId));

        return new FullProfileSO(
                profileId,
                profile.getName(),
                profile.getProfileType(),
                profile.getStatus()
        );
    }

    @Override
    public UpdatedProfileSO updateProfile(UpdateProfileSO updatedProfileSO) {
        checkExistence(updatedProfileSO.profileId());

        Profile profile = profileRepository.getReferenceById(UUID.fromString(updatedProfileSO.profileId()));

        profile.setName(updatedProfileSO.name());
        profile.setProfileType(updatedProfileSO.profileType());
        profile.setStatus(updatedProfileSO.status());

        profileRepository.save(profile);

        return new UpdatedProfileSO(
                updatedProfileSO.profileId(),
                updatedProfileSO.name(),
                updatedProfileSO.profileType(),
                updatedProfileSO.status()
        );
    }

    @Override
    public void suspendProfile(String profileId) {
        checkExistence(profileId);

        Profile profile = profileRepository.getReferenceById(UUID.fromString(profileId));

        profile.setStatus(Status.SUSPENDED);

        profileRepository.save(profile);
    }

    @Override
    public boolean profileExists(String profileId) {
        return profileRepository.existsById(UUID.fromString(profileId));
    }

    private void checkExistence(String profileId) {
        if (!profileRepository.existsById(UUID.fromString(profileId))) {
            log.debug("Profile with id " + profileId + " was not found");
            throw new HttpStatusCodeException(
                    new NotFoundErrorCause(List.of(HttpErrorCause.NotFound.PROFILE_NOT_FOUND)),
                    profileId
            );
        }
        log.debug("Profile with id " + profileId + " was successfully found");
    }
}
