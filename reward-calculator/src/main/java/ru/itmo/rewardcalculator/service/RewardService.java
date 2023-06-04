package ru.itmo.rewardcalculator.service;

import ru.itmo.rewardcalculator.service.so.in.CalcInfoSO;
import ru.itmo.rewardcalculator.service.so.in.PeriodFilterSO;
import ru.itmo.rewardcalculator.service.so.out.*;

import java.math.BigDecimal;
import java.util.List;

public interface RewardService {
    void calcRewardFor(CalcInfoSO calcInfo);
    FullRewardSO getReward(String id);
    TotalsSO getTotals(PeriodFilterSO filter);
    TotalsSO getPointTotals(String pointId, PeriodFilterSO filter);
    TotalsSO getProfileTotals(String profileId, PeriodFilterSO filter);
    List<HistoryTotalsSO> getHistoryTotals(PeriodFilterSO filter);
    List<HistoryTotalsSO> getPointHistoryTotals(String pointId, PeriodFilterSO filter);
    List<HistoryTotalsSO> getProfileHistoryTotals(String profileId, PeriodFilterSO filter);
    BigDecimal findAvgReward(PeriodFilterSO filter);
    BigDecimal findProfileAvgReward(String profileId, PeriodFilterSO filter);
    BigDecimal findPointAvgReward(String pointId, PeriodFilterSO filter);
    MaxRewardSO findProfileMaxReward(String profileId, PeriodFilterSO filter);
    MinRewardSO findProfileMinReward(String profileId, PeriodFilterSO filter);
    MaxRewardSO findPointMaxReward(String pointId, PeriodFilterSO filter);
    MinRewardSO findPointMinReward(String pointId, PeriodFilterSO filter);
    ExtremeRewardSO findExtremeReward(PeriodFilterSO filter);
    ExtremeRewardSO findProfileExtremeReward(String profileId, PeriodFilterSO filter);
    ExtremeRewardSO findPointExtremeReward(String pointId, PeriodFilterSO filter);
}
