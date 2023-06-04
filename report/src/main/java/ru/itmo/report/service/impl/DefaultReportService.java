package ru.itmo.report.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.common.web.client.RewardCalculatorClient;
import ru.itmo.report.service.ReportService;
import ru.itmo.rewardcalculator.service.so.in.PeriodFilterSO;
import ru.itmo.rewardcalculator.service.so.out.ExtremeRewardSO;
import ru.itmo.rewardcalculator.service.so.out.MaxRewardSO;
import ru.itmo.rewardcalculator.service.so.out.MinRewardSO;
import ru.itmo.rewardcalculator.service.so.out.TotalsSO;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DefaultReportService implements ReportService {
    private final RewardCalculatorClient rewardCalculatorClient;

    @Override
    public TotalsSO getTotals(PeriodFilterSO filter) {
        var result = rewardCalculatorClient.getTotals(PeriodFilterSO.toPeriodFilter(filter));
        return new TotalsSO(result.eventAmountTotal(), result.rewardAmountTotal());
    }

    @Override
    public TotalsSO getProfileTotals(String profileId, PeriodFilterSO filter) {
        var result = rewardCalculatorClient.getProfileTotals(profileId, PeriodFilterSO.toPeriodFilter(filter));
        return new TotalsSO(result.eventAmountTotal(), result.rewardAmountTotal());
    }

    @Override
    public TotalsSO getPointTotals(String pointId, PeriodFilterSO filter) {
        var result = rewardCalculatorClient.getPointTotals(pointId, PeriodFilterSO.toPeriodFilter(filter));
        return new TotalsSO(result.eventAmountTotal(), result.rewardAmountTotal());
    }

    @Override
    public BigDecimal findAvgReward(PeriodFilterSO filter) {
        return rewardCalculatorClient.getAvg(PeriodFilterSO.toPeriodFilter(filter));
    }

    @Override
    public BigDecimal findProfileAvgReward(String profileId, PeriodFilterSO filter) {
        return rewardCalculatorClient.getProfileAvg(profileId, PeriodFilterSO.toPeriodFilter(filter));
    }

    @Override
    public BigDecimal findPointAvgReward(String pointId, PeriodFilterSO filter) {
        return rewardCalculatorClient.getPointAvg(pointId, PeriodFilterSO.toPeriodFilter(filter));
    }

    @Override
    public MaxRewardSO findMaxReward(PeriodFilterSO filter) {
        var result = rewardCalculatorClient.getMaxReward(PeriodFilterSO.toPeriodFilter(filter));
        return new MaxRewardSO(result.pointId(), result.amount(), result.period());
    }

    @Override
    public MinRewardSO findMinReward(PeriodFilterSO filter) {
        var result = rewardCalculatorClient.getMinReward(PeriodFilterSO.toPeriodFilter(filter));
        return new MinRewardSO(result.pointId(), result.amount(), result.period());
    }

    @Override
    public MaxRewardSO findProfileMaxReward(String profileId, PeriodFilterSO filter) {
        var result = rewardCalculatorClient.getProfileMaxReward(profileId, PeriodFilterSO.toPeriodFilter(filter));
        return new MaxRewardSO(result.pointId(), result.amount(), result.period());
    }

    @Override
    public MinRewardSO findProfileMinReward(String profileId, PeriodFilterSO filter) {
        var result = rewardCalculatorClient.getProfileMinReward(profileId, PeriodFilterSO.toPeriodFilter(filter));
        return new MinRewardSO(result.pointId(), result.amount(), result.period());
    }

    @Override
    public MaxRewardSO findPointMaxReward(String pointId, PeriodFilterSO filter) {
        var result = rewardCalculatorClient.getPointMaxReward(pointId, PeriodFilterSO.toPeriodFilter(filter));
        return new MaxRewardSO(result.pointId(), result.amount(), result.period());
    }

    @Override
    public MinRewardSO findPointMinReward(String pointId, PeriodFilterSO filter) {
        var result = rewardCalculatorClient.getPointMinReward(pointId, PeriodFilterSO.toPeriodFilter(filter));
        return new MinRewardSO(result.pointId(), result.amount(), result.period());
    }

    @Override
    public ExtremeRewardSO findExtremeReward(PeriodFilterSO filter) {
        var result = rewardCalculatorClient.getExtremes(PeriodFilterSO.toPeriodFilter(filter));
        return new ExtremeRewardSO(
                new MaxRewardSO(result.max().pointId(), result.max().amount(), result.max().period()),
                new MinRewardSO(result.min().pointId(), result.min().amount(), result.min().period())
        );
    }

    @Override
    public ExtremeRewardSO findProfileExtremeReward(String profileId, PeriodFilterSO filter) {
        var result = rewardCalculatorClient.getProfileExtremes(profileId, PeriodFilterSO.toPeriodFilter(filter));
        return new ExtremeRewardSO(
                new MaxRewardSO(result.max().pointId(), result.max().amount(), result.max().period()),
                new MinRewardSO(result.min().pointId(), result.min().amount(), result.min().period())
        );
    }

    @Override
    public ExtremeRewardSO findPointExtremeReward(String pointId, PeriodFilterSO filter) {
        var result = rewardCalculatorClient.getPointExtremes(pointId, PeriodFilterSO.toPeriodFilter(filter));
        return new ExtremeRewardSO(
                new MaxRewardSO(result.max().pointId(), result.max().amount(), result.max().period()),
                new MinRewardSO(result.min().pointId(), result.min().amount(), result.min().period())
        );
    }
}
