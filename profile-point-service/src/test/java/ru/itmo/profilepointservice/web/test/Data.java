package ru.itmo.profilepointservice.web.test;

import ru.itmo.profilepointservice.domain.enumeration.ProfileType;
import ru.itmo.profilepointservice.domain.enumeration.Status;
import ru.itmo.profilepointservice.domain.enumeration.PointType;
import ru.itmo.profilepointservice.web.dto.request.CreateProfileRequest;

public final class Data {
    public static final String SAMPLE_PROFILE_NAME = "sample";
    public static final ProfileType SAMPLE_PROFILE_TYPE = ProfileType.PAYMENT_PARTNER;
    public static final String SAMPLE_POINT_NAME = "pointSample";
    public static final PointType SAMPLE_POINT_TYPE = PointType.PAYMENT_RECEPTION;
    public static final Status SAMPLE_STATUS = Status.INACTIVE;

    public static final CreateProfileRequest SAMPLE_PROFILE_REQUEST = new CreateProfileRequest(
            SAMPLE_PROFILE_NAME,
            SAMPLE_PROFILE_TYPE,
            SAMPLE_STATUS
    );
}
