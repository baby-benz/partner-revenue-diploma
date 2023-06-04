package ru.itmo.report.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.common.web.client.RewardCalculatorClient;
import ru.itmo.common.web.dto.response.rewardcalculator.ExtremeReward;
import ru.itmo.common.web.dto.response.rewardcalculator.MaxReward;
import ru.itmo.common.web.dto.response.rewardcalculator.MinReward;
import ru.itmo.common.web.dto.response.rewardcalculator.Totals;
import ru.itmo.rewardcalculator.service.so.out.ExtremeRewardSO;
import ru.itmo.rewardcalculator.service.so.out.MaxRewardSO;
import ru.itmo.rewardcalculator.service.so.out.MinRewardSO;
import ru.itmo.rewardcalculator.service.so.out.TotalsSO;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class DefaultReportServiceTest {
    @Mock
    RewardCalculatorClient client;
    @InjectMocks
    DefaultReportService service;

    @Test
    void when_getTotals_then_ok() {
        final Totals totals = new Totals(BigDecimal.valueOf(777.77d), BigDecimal.valueOf(77.77d));

        doReturn(totals).when(client).getTotals(any());

        final TotalsSO expectedTotals = new TotalsSO(totals.eventAmountTotal(), totals.rewardAmountTotal());

        assertEquals(expectedTotals, service.getTotals(null));
    }

    @Test
    void when_getProfileTotals_then_ok() {
        String profileId = UUID.randomUUID().toString();

        final Totals totals = new Totals(BigDecimal.valueOf(777.77d), BigDecimal.valueOf(77.77d));

        doReturn(totals).when(client).getProfileTotals(profileId, null);

        final TotalsSO expectedTotals = new TotalsSO(totals.eventAmountTotal(), totals.rewardAmountTotal());

        assertEquals(expectedTotals, service.getProfileTotals(profileId, null));
    }

    @Test
    void when_getPointTotals_then_ok() {
        String pointId = UUID.randomUUID().toString();

        final Totals totals = new Totals(BigDecimal.valueOf(777.77d), BigDecimal.valueOf(77.77d));

        doReturn(totals).when(client).getPointTotals(pointId, null);

        final TotalsSO expectedTotals = new TotalsSO(totals.eventAmountTotal(), totals.rewardAmountTotal());

        assertEquals(expectedTotals, service.getPointTotals(pointId, null));
    }

    @Test
    void when_findExtremes_then_ok() {
        String point1Id = UUID.randomUUID().toString();
        String point2Id = UUID.randomUUID().toString();

        final ExtremeReward extremeReward = new ExtremeReward(
                new MaxReward(point1Id, BigDecimal.valueOf(777.77d), YearMonth.now()),
                new MinReward(point2Id, BigDecimal.valueOf(77.77d), YearMonth.now())
        );

        doReturn(extremeReward).when(client).getExtremes(any());

        final ExtremeRewardSO expectedExtreme = new ExtremeRewardSO(
                new MaxRewardSO(extremeReward.max().pointId(), extremeReward.max().amount(), extremeReward.max().period()),
                new MinRewardSO(extremeReward.min().pointId(), extremeReward.min().amount(), extremeReward.min().period())
        );

        assertEquals(expectedExtreme, service.findExtremeReward(null));
    }

    @Test
    void when_findProfileExtremes_then_ok() {
        String profileId = UUID.randomUUID().toString();
        String point1Id = UUID.randomUUID().toString();
        String point2Id = UUID.randomUUID().toString();

        final ExtremeReward extremeReward = new ExtremeReward(
                new MaxReward(point1Id, BigDecimal.valueOf(777.77d), YearMonth.now()),
                new MinReward(point2Id, BigDecimal.valueOf(77.77d), YearMonth.now())
        );

        doReturn(extremeReward).when(client).getProfileExtremes(profileId, null);

        final ExtremeRewardSO expectedExtreme = new ExtremeRewardSO(
                new MaxRewardSO(extremeReward.max().pointId(), extremeReward.max().amount(), extremeReward.max().period()),
                new MinRewardSO(extremeReward.min().pointId(), extremeReward.min().amount(), extremeReward.min().period())
        );

        assertEquals(expectedExtreme, service.findProfileExtremeReward(profileId,null));
    }

    @Test
    void when_findPointExtremes_then_ok() {
        String pointId = UUID.randomUUID().toString();

        final ExtremeReward extremeReward = new ExtremeReward(
                new MaxReward(pointId, BigDecimal.valueOf(777.77d), YearMonth.now()),
                new MinReward(pointId, BigDecimal.valueOf(77.77d), YearMonth.now().minusMonths(1))
        );

        doReturn(extremeReward).when(client).getPointExtremes(pointId, null);

        final ExtremeRewardSO expectedExtreme = new ExtremeRewardSO(
                new MaxRewardSO(extremeReward.max().pointId(), extremeReward.max().amount(), extremeReward.max().period()),
                new MinRewardSO(extremeReward.min().pointId(), extremeReward.min().amount(), extremeReward.min().period())
        );

        assertEquals(expectedExtreme, service.findPointExtremeReward(pointId, null));
    }

    @Test
    void when_findAvg_then_ok() {
        final BigDecimal avg = BigDecimal.valueOf(777.77d);

        doReturn(avg).when(client).getAvg(any());

        assertEquals(avg, service.findAvgReward(null));
    }

    @Test
    void when_findProfileAvg_then_ok() {
        String profileId = UUID.randomUUID().toString();

        final BigDecimal avg = BigDecimal.valueOf(777.77d);

        doReturn(avg).when(client).getProfileAvg(profileId, null);

        assertEquals(avg, service.findProfileAvgReward(profileId, null));
    }

    @Test
    void when_findPointAvg_then_ok() {
        String pointId = UUID.randomUUID().toString();

        final BigDecimal avg = BigDecimal.valueOf(777.77d);

        doReturn(avg).when(client).getPointAvg(pointId, null);

        assertEquals(avg, service.findPointAvgReward(pointId, null));
    }

    @Test
    void when_getMaxReward_then_ok() {
        String profileId = UUID.randomUUID().toString();
        String pointId = UUID.randomUUID().toString();

        final MaxReward max = new MaxReward(UUID.randomUUID().toString(), BigDecimal.valueOf(777.77d), YearMonth.now());

        doReturn(max).when(client).getMaxReward(any());
        doReturn(max).when(client).getProfileMaxReward(profileId, null);
        doReturn(max).when(client).getPointMaxReward(pointId, null);

        final MaxRewardSO expectedMax = new MaxRewardSO(max.pointId(), max.amount(), max.period());

        assertEquals(expectedMax, service.findMaxReward(null));
        assertEquals(expectedMax, service.findProfileMaxReward(profileId, null));
        assertEquals(expectedMax, service.findPointMaxReward(pointId, null));
    }

    @Test
    void when_getMinReward_then_ok() {
        String pointId = UUID.randomUUID().toString();

        final MinReward min = new MinReward(pointId, BigDecimal.valueOf(777.77d), YearMonth.now());

        doReturn(min).when(client).getMinReward(any());

        final MinRewardSO expectedMin = new MinRewardSO(min.pointId(), min.amount(), min.period());

        assertEquals(expectedMin, service.findMinReward(null));
    }

    @Test
    void when_getProfileMinReward_then_ok() {
        String profileId = UUID.randomUUID().toString();
        String pointId = UUID.randomUUID().toString();

        final MinReward min = new MinReward(pointId, BigDecimal.valueOf(777.77d), YearMonth.now());

        doReturn(min).when(client).getProfileMinReward(profileId, null);

        final MinRewardSO expectedMin = new MinRewardSO(min.pointId(), min.amount(), min.period());

        assertEquals(expectedMin, service.findProfileMinReward(profileId, null));
    }

    @Test
    void when_getPointMinReward_then_ok() {
        String pointId = UUID.randomUUID().toString();

        final MinReward min = new MinReward(pointId, BigDecimal.valueOf(777.77d), YearMonth.now());

        doReturn(min).when(client).getPointMinReward(pointId, null);

        final MinRewardSO expectedMin = new MinRewardSO(min.pointId(), min.amount(), min.period());

        assertEquals(expectedMin, service.findPointMinReward(pointId, null));
    }
}
