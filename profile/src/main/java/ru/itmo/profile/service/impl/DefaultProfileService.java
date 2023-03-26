package ru.itmo.profile.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.profile.domain.entity.Profile;
import ru.itmo.profile.repository.ProfileRepository;
import ru.itmo.profile.service.ProfileService;
import ru.itmo.profile.service.so.in.CreateProfileSO;
import ru.itmo.profile.service.so.out.CreatedProfileSO;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DefaultProfileService implements ProfileService {
    private final ProfileRepository profileRepository;

    @Override
    public CreatedProfileSO createProfile(CreateProfileSO profileData) {
        Profile createdProfile = profileRepository.save(
                new Profile(
                        UUID.randomUUID().toString(),
                        profileData.name(),
                        profileData.profileType(),
                        profileData.status()
                )
        );
        return new CreatedProfileSO(
                createdProfile.getId(),
                createdProfile.getName(),
                createdProfile.getProfileType(),
                createdProfile.getStatus()
        );
    }

    @Override
    public void checkProfile(String profileId) {
        if (!profileRepository.existsById(profileId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
