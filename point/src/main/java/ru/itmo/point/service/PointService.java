package ru.itmo.point.service;

import ru.itmo.point.service.so.in.CreatePointSO;
import ru.itmo.point.service.so.in.UpdatePointSO;
import ru.itmo.point.service.so.out.CreatedPointSO;
import ru.itmo.point.service.so.out.FullPointSO;
import ru.itmo.point.service.so.out.UpdatedPointSO;

public interface PointService {
    CreatedPointSO createPoint(CreatePointSO pointData);
    FullPointSO getPoint(String pointId);
    UpdatedPointSO updatePoint(UpdatePointSO pointData);
}
