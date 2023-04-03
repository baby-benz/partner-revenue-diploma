package ru.itmo.profile.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
        var profileToCreate = new Profile(UUID.randomUUID().toString(), profileData.name(), profileData.profileType());

        if (profileData.status() != null) {
            profileToCreate.setStatus(profileData.status());
        }

        Profile createdProfile = profileRepository.save(profileToCreate);

        return new CreatedProfileSO(
                createdProfile.getId(),
                createdProfile.getName(),
                createdProfile.getProfileType(),
                createdProfile.getStatus()
        );
    }

    @Override
    public boolean profileExists(String profileId) {
        return profileRepository.existsById(profileId);
    }
}
