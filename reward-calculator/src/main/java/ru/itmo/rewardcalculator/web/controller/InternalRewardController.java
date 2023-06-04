package ru.itmo.rewardcalculator.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itmo.common.constant.InternalEndpoint;
import ru.itmo.common.web.dto.request.PeriodFilter;
import ru.itmo.common.web.validation.ValidNullableUUID;
import ru.itmo.rewardcalculator.service.RewardService;
import ru.itmo.rewardcalculator.service.so.in.PeriodFilterSO;
import ru.itmo.rewardcalculator.service.so.out.ExtremeRewardSO;
import ru.itmo.rewardcalculator.service.so.out.HistoryTotalsSO;
import ru.itmo.rewardcalculator.service.so.out.TotalsSO;
import ru.itmo.rewardcalculator.web.dto.response.*;
import ru.itmo.common.web.validation.ValidPeriodFilter;

import java.math.BigDecimal;
import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
public class InternalRewardController {
    private final RewardService rewardService;

    @GetMapping(value = InternalEndpoint.RewardCalculator.GET_TOTALS, produces = MediaType.APPLICATION_JSON_VALUE)
    public TotalsResponse getTotals(@RequestBody(required = false) @ValidPeriodFilter PeriodFilter filter,
                                    @RequestParam(required = false) @ValidNullableUUID String profileId,
                                    @RequestParam(required = false) @ValidNullableUUID String pointId) {
        TotalsSO totals;

        if (profileId != null) {
            totals = rewardService.getProfileTotals(profileId, PeriodFilterSO.fromPeriodFilter(filter));
        } else if (pointId != null) {
            totals = rewardService.getPointTotals(pointId, PeriodFilterSO.fromPeriodFilter(filter));
        } else {
            totals = rewardService.getTotals(PeriodFilterSO.fromPeriodFilter(filter));
        }

        return new TotalsResponse(totals.eventAmountTotal(), totals.rewardAmountTotal());
    }

    @GetMapping(value = InternalEndpoint.RewardCalculator.GET_HISTORY_TOTALS, produces = MediaType.APPLICATION_JSON_VALUE)
    public HistoryTotalsResponse getHistoryTotals(@RequestBody(required = false) @ValidPeriodFilter PeriodFilter filter,
                                                  @RequestParam(required = false) @ValidNullableUUID String profileId,
                                                  @RequestParam(required = false) @ValidNullableUUID String pointId) {
        List<HistoryTotalsSO> totals;

        if (profileId != null) {
            totals = rewardService.getProfileHistoryTotals(profileId, PeriodFilterSO.fromPeriodFilter(filter));
        } else if (pointId != null) {
            totals = rewardService.getPointHistoryTotals(pointId, PeriodFilterSO.fromPeriodFilter(filter));
        } else {
            totals = rewardService.getHistoryTotals(PeriodFilterSO.fromPeriodFilter(filter));
        }

        List<HistoryTotalsResponse.TotalsWithDateResponse> totalsResponse = totals.stream().map(historyTotalsSO ->
                new HistoryTotalsResponse.TotalsWithDateResponse(
                        historyTotalsSO.eventAmountTotal(),
                        historyTotalsSO.rewardAmountTotal(),
                        historyTotalsSO.period()
                )
        ).toList();

        return new HistoryTotalsResponse(totalsResponse, filter.start(), filter.end());
    }

    @GetMapping(value = InternalEndpoint.RewardCalculator.GET_AVG, produces = MediaType.APPLICATION_JSON_VALUE)
    public AvgRewardResponse getAvg(@RequestBody(required = false) @ValidPeriodFilter PeriodFilter filter,
                                    @RequestParam(required = false) @ValidNullableUUID String profileId,
                                    @RequestParam(required = false) @ValidNullableUUID String pointId) {
        BigDecimal avg;

        if (profileId != null) {
            avg = rewardService.findProfileAvgReward(profileId, PeriodFilterSO.fromPeriodFilter(filter));
        } else if (pointId != null) {
            avg = rewardService.findPointAvgReward(pointId, PeriodFilterSO.fromPeriodFilter(filter));
        } else {
            avg = rewardService.findAvgReward(PeriodFilterSO.fromPeriodFilter(filter));
        }

        return new AvgRewardResponse(avg, filter.start(), filter.end());
    }

    @GetMapping(value = InternalEndpoint.RewardCalculator.GET_EXTREMES, produces = MediaType.APPLICATION_JSON_VALUE)
    public ExtremeRewardResponse getExtremes(@RequestBody(required = false) @ValidPeriodFilter PeriodFilter filter,
                                                    @RequestParam(required = false) @ValidNullableUUID String profileId,
                                                    @RequestParam(required = false) @ValidNullableUUID String pointId) {
        ExtremeRewardSO extreme;

        if (profileId != null) {
            extreme = rewardService.findProfileExtremeReward(profileId, PeriodFilterSO.fromPeriodFilter(filter));
        } else if (pointId != null) {
            extreme = rewardService.findPointExtremeReward(pointId, PeriodFilterSO.fromPeriodFilter(filter));
        } else {
            extreme = rewardService.findExtremeReward(PeriodFilterSO.fromPeriodFilter(filter));
        }

        return new ExtremeRewardResponse(
                new MaxRewardResponse(extreme.max().pointId(), extreme.max().amount(), extreme.max().period()),
                new MinRewardResponse(extreme.min().pointId(), extreme.min().amount(), extreme.min().period()),
                filter
        );
    }
}
