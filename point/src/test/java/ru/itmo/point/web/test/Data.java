package ru.itmo.point.web.test;

import ru.itmo.common.domain.enumeration.Status;
import ru.itmo.point.domain.enumeration.PointType;
import ru.itmo.point.web.dto.request.CreatePointRequest;

public final class Data {
    public static final String SAMPLE_POINT_NAME = "pointSample";
    public static final String SAMPLE_PROFILE_ID = "profileSample";
    public static final PointType SAMPLE_POINT_TYPE = PointType.PAYMENT_RECEPTION;
    public static final Status SAMPLE_STATUS = Status.INACTIVE;

    public static final CreatePointRequest SAMPLE_POINT_REQUEST = new CreatePointRequest(
            SAMPLE_PROFILE_ID,
            SAMPLE_POINT_NAME,
            SAMPLE_POINT_TYPE,
            SAMPLE_STATUS,
            null
    );
}
