package ru.itmo.profile.service;

import ru.itmo.profile.service.so.in.CreateProfileSO;
import ru.itmo.profile.service.so.out.CreatedProfileSO;

public interface ProfileService {
    CreatedProfileSO createProfile(CreateProfileSO profileData);
    boolean profileExists(String profileId);
}
