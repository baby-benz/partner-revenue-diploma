package ru.itmo.profilepointservice.service;

import ru.itmo.profilepointservice.domain.enumeration.Status;
import ru.itmo.profilepointservice.service.so.in.CreatePointSO;
import ru.itmo.profilepointservice.service.so.in.UpdatePointSO;
import ru.itmo.profilepointservice.service.so.out.CreatedPointSO;
import ru.itmo.profilepointservice.service.so.out.FullPointSO;
import ru.itmo.profilepointservice.service.so.out.RequestedFieldsPointSO;
import ru.itmo.profilepointservice.service.so.out.UpdatedPointSO;
import ru.itmo.profilepointservice.web.dto.request.ResponsePointFields;

import java.util.List;

public interface PointService {
    CreatedPointSO createPoint(CreatePointSO pointData);

    FullPointSO getPoint(String pointId);

    UpdatedPointSO updatePoint(UpdatePointSO pointData);

    boolean pointMatchesProfile(String pointId, String profileId);

    UpdatedPointSO setCalcScheme(String pointId, String calcSchemeId);

    UpdatedPointSO updateStatus(String pointId, Status status);

    RequestedFieldsPointSO retrieveRequestedSetOfFields(String pointId, ResponsePointFields[] fieldsList);
    List<String> getPointIdsByProfileId(String profileId);
}
