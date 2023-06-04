package ru.itmo.common.web.client;

import ru.itmo.common.web.dto.request.PeriodFilter;
import ru.itmo.common.web.dto.response.rewardcalculator.MaxReward;
import ru.itmo.common.web.dto.response.rewardcalculator.MinReward;
import ru.itmo.common.web.dto.response.rewardcalculator.Totals;
import ru.itmo.common.web.dto.response.rewardcalculator.ExtremeReward;

import java.math.BigDecimal;

public interface RewardCalculatorClient {
    Totals getTotals(PeriodFilter filter);
    Totals getProfileTotals(String profileId, PeriodFilter filter);
    Totals getPointTotals(String pointId, PeriodFilter filter);
    BigDecimal getAvg(PeriodFilter filter);
    BigDecimal getProfileAvg(String profileId, PeriodFilter filter);
    BigDecimal getPointAvg(String pointId, PeriodFilter filter);
    MaxReward getMaxReward(PeriodFilter filter);
    MaxReward getProfileMaxReward(String profileId, PeriodFilter filter);
    MaxReward getPointMaxReward(String pointId, PeriodFilter filter);
    MinReward getMinReward(PeriodFilter filter);
    MinReward getProfileMinReward(String profileId, PeriodFilter filter);
    MinReward getPointMinReward(String pointId, PeriodFilter filter);
    ExtremeReward getExtremes(PeriodFilter filter);
    ExtremeReward getProfileExtremes(String profileId, PeriodFilter filter);
    ExtremeReward getPointExtremes(String pointId, PeriodFilter filter);
}
