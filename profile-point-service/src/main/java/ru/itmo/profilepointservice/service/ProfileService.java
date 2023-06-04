package ru.itmo.profilepointservice.service;

import ru.itmo.profilepointservice.service.so.in.CreateProfileSO;
import ru.itmo.profilepointservice.service.so.in.UpdateProfileSO;
import ru.itmo.profilepointservice.service.so.out.CreatedProfileSO;
import ru.itmo.profilepointservice.service.so.out.FullProfileSO;
import ru.itmo.profilepointservice.service.so.out.UpdatedProfileSO;

public interface ProfileService {
    CreatedProfileSO createProfile(CreateProfileSO profileData);
    FullProfileSO getProfile(String profileId);
    UpdatedProfileSO updateProfile(UpdateProfileSO updatedProfileSO);
    void suspendProfile(String profileId);
    boolean profileExists(String profileId);
}
