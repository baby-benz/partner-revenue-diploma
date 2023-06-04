package ru.itmo.report.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.itmo.common.web.dto.request.PeriodFilter;
import ru.itmo.common.web.validation.ValidNullableUUID;
import ru.itmo.common.web.validation.ValidPeriodFilter;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.report.service.ReportService;
import ru.itmo.report.web.dto.response.*;
import ru.itmo.rewardcalculator.service.so.in.PeriodFilterSO;
import ru.itmo.rewardcalculator.service.so.out.ExtremeRewardSO;
import ru.itmo.rewardcalculator.service.so.out.MaxRewardSO;
import ru.itmo.rewardcalculator.service.so.out.MinRewardSO;
import ru.itmo.rewardcalculator.service.so.out.TotalsSO;

import java.math.BigDecimal;

@RequiredArgsConstructor
@RestController
public class ReportController {
    private final ReportService reportService;

    @GetMapping(value = Endpoint.Report.GET_TOTALS, produces = MediaType.APPLICATION_JSON_VALUE)
    public TotalsResponse getTotals(@RequestBody(required = false) @ValidPeriodFilter PeriodFilter filter,
                                    @RequestParam(required = false) @ValidNullableUUID String profileId,
                                    @RequestParam(required = false) @ValidNullableUUID String pointId) {
        TotalsSO totals;
        if (profileId != null) {
            totals = reportService.getPointTotals(profileId, PeriodFilterSO.fromPeriodFilter(filter));
        } else if (pointId != null) {
            totals = reportService.getPointTotals(pointId, PeriodFilterSO.fromPeriodFilter(filter));
        } else {
            totals = reportService.getTotals(PeriodFilterSO.fromPeriodFilter(filter));
        }
        return new TotalsResponse(totals.eventAmountTotal(), totals.rewardAmountTotal());
    }

    @GetMapping(value = Endpoint.Report.GET_AVG_REWARD, produces = MediaType.APPLICATION_JSON_VALUE)
    public AvgRewardResponse getAvgReward(@RequestBody(required = false) @ValidPeriodFilter PeriodFilter filter,
                                          @RequestParam(required = false) @ValidNullableUUID String profileId,
                                          @RequestParam(required = false) @ValidNullableUUID String pointId) {
        BigDecimal avg;
        if (profileId != null) {
            avg = reportService.findProfileAvgReward(profileId, PeriodFilterSO.fromPeriodFilter(filter));
        } else if (pointId != null) {
            avg = reportService.findPointAvgReward(pointId, PeriodFilterSO.fromPeriodFilter(filter));
        } else {
            avg = reportService.findAvgReward(PeriodFilterSO.fromPeriodFilter(filter));
        }
        return new AvgRewardResponse(avg, filter.start(), filter.end());
    }

    @GetMapping(value = Endpoint.Report.GET_MAX_REWARD, produces = MediaType.APPLICATION_JSON_VALUE)
    public MaxRewardResponse getMaxReward(@RequestBody(required = false) @ValidPeriodFilter PeriodFilter filter,
                                          @RequestParam(required = false) @ValidNullableUUID String profileId,
                                          @RequestParam(required = false) @ValidNullableUUID String pointId) {
        MaxRewardSO max;
        if (profileId != null) {
            max = reportService.findProfileMaxReward(profileId, PeriodFilterSO.fromPeriodFilter(filter));
        } else if (pointId != null) {
            max = reportService.findPointMaxReward(pointId, PeriodFilterSO.fromPeriodFilter(filter));
        } else {
            max = reportService.findMaxReward(PeriodFilterSO.fromPeriodFilter(filter));
        }
        return new MaxRewardResponse(max.pointId(), max.amount(), max.period());
    }

    @GetMapping(value = Endpoint.Report.GET_MIN_REWARD, produces = MediaType.APPLICATION_JSON_VALUE)
    public MinRewardResponse getMinReward(@RequestBody(required = false) @ValidPeriodFilter PeriodFilter filter,
                                          @RequestParam(required = false) @ValidNullableUUID String profileId,
                                          @RequestParam(required = false) @ValidNullableUUID String pointId) {
        MinRewardSO min;
        if (profileId != null) {
            min = reportService.findProfileMinReward(profileId, PeriodFilterSO.fromPeriodFilter(filter));
        } else if (pointId != null) {
            min = reportService.findPointMinReward(pointId, PeriodFilterSO.fromPeriodFilter(filter));
        } else {
            min = reportService.findMinReward(PeriodFilterSO.fromPeriodFilter(filter));
        }
        return new MinRewardResponse(min.pointId(), min.amount(), min.period());
    }

    @GetMapping(value = Endpoint.Report.GET_EXTREME_REWARD, produces = MediaType.APPLICATION_JSON_VALUE)
    public ExtremeRewardResponse getExtremeReward(@RequestBody(required = false) @ValidPeriodFilter PeriodFilter filter,
                                                  @RequestParam(required = false) @ValidNullableUUID String profileId,
                                                  @RequestParam(required = false) @ValidNullableUUID String pointId) {
        ExtremeRewardSO extreme;
        if (profileId != null) {
            extreme = reportService.findProfileExtremeReward(profileId, PeriodFilterSO.fromPeriodFilter(filter));
        } else if (pointId != null) {
            extreme = reportService.findPointExtremeReward(pointId, PeriodFilterSO.fromPeriodFilter(filter));
        } else {
            extreme = reportService.findExtremeReward(PeriodFilterSO.fromPeriodFilter(filter));
        }
        return new ExtremeRewardResponse(
                new MaxRewardResponse(extreme.max().pointId(), extreme.max().amount(), extreme.max().period()),
                new MinRewardResponse(extreme.min().pointId(), extreme.min().amount(), extreme.max().period()),
                PeriodFilterSO.fromPeriodFilter(filter)
        );
    }
}
