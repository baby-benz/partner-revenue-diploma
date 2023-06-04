package ru.itmo.report.service;

import ru.itmo.rewardcalculator.service.so.in.PeriodFilterSO;
import ru.itmo.rewardcalculator.service.so.out.ExtremeRewardSO;
import ru.itmo.rewardcalculator.service.so.out.MaxRewardSO;
import ru.itmo.rewardcalculator.service.so.out.MinRewardSO;
import ru.itmo.rewardcalculator.service.so.out.TotalsSO;

import java.math.BigDecimal;

public interface ReportService {
    TotalsSO getTotals(PeriodFilterSO filter);
    TotalsSO getProfileTotals(String profileId, PeriodFilterSO filter);
    TotalsSO getPointTotals(String pointId, PeriodFilterSO filter);
    BigDecimal findAvgReward(PeriodFilterSO filter);
    BigDecimal findProfileAvgReward(String profileId, PeriodFilterSO filter);
    BigDecimal findPointAvgReward(String pointId, PeriodFilterSO filter);
    MaxRewardSO findMaxReward(PeriodFilterSO filter);
    MinRewardSO findMinReward(PeriodFilterSO filter);
    MaxRewardSO findProfileMaxReward(String profileId, PeriodFilterSO filter);
    MinRewardSO findProfileMinReward(String profileId, PeriodFilterSO filter);
    MaxRewardSO findPointMaxReward(String pointId, PeriodFilterSO filter);
    MinRewardSO findPointMinReward(String pointId, PeriodFilterSO filter);
    ExtremeRewardSO findExtremeReward(PeriodFilterSO filter);
    ExtremeRewardSO findProfileExtremeReward(String profileId, PeriodFilterSO filter);
    ExtremeRewardSO findPointExtremeReward(String pointId, PeriodFilterSO filter);
}
