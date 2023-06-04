package ru.itmo.common.web.client;

import java.util.List;

public interface ProfilePointClient {
    String getProfileIdByPointId(String pointId);
    boolean pointAndProfileMatches(String pointId, String profileId);
    String getCalcSchemeId(String pointId);
    void checkProfileExistence(String profileId);
    List<String> getPointIdsWithProfileId(String profileId);
}
