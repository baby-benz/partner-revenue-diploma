package ru.itmo.rewardcalculator.service.impl;

import com.ezylang.evalex.Expression;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.common.domain.message.CalcInfo;
import ru.itmo.common.domain.message.CalculatedReward;
import ru.itmo.common.domain.message.EventStatus;
import ru.itmo.common.domain.message.FailedCalcInfo;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.HttpErrorCause;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.service.util.BigDecimalUtil;
import ru.itmo.common.web.client.CalcSchemeClient;
import ru.itmo.common.web.client.ProfilePointClient;
import ru.itmo.common.web.dto.response.calcscheme.CalcScheme;
import ru.itmo.rewardcalculator.domain.entity.Reward;
import ru.itmo.rewardcalculator.domain.entity.RewardHistoryMetrics;
import ru.itmo.rewardcalculator.kafka.producer.CalculatedRewardProducer;
import ru.itmo.rewardcalculator.kafka.producer.FailedEventProducer;
import ru.itmo.rewardcalculator.kafka.producer.EventStatusProducer;
import ru.itmo.rewardcalculator.repository.RewardHistoryMetricsRepository;
import ru.itmo.rewardcalculator.repository.RewardRepository;
import ru.itmo.rewardcalculator.service.RewardCalculator;
import ru.itmo.rewardcalculator.service.RewardService;
import ru.itmo.rewardcalculator.service.so.in.CalcInfoSO;
import ru.itmo.rewardcalculator.service.so.in.PeriodFilterSO;
import ru.itmo.rewardcalculator.service.so.out.*;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.*;
import java.math.BigDecimal;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class DefaultRewardService implements RewardService {
    @Value("${calc.expression}")
    private String calculationExpressionRaw;
    @Value("${calc.amount.code-word}")
    private String amountCodeWord;
    @Value("${calc.interest-rate.code-word}")
    private String interestRateCodeWord;
    @Value("${calc.bonus.code-word}")
    private String bonusCodeWord;
    private RewardCalculator rewardCalculator;
    private final Map<String, RewardMetrics> currentRewardInfos = new HashMap<>();

    private final ProfilePointClient profilePointClient;
    private final CalcSchemeClient calcSchemeClient;
    private final RewardRepository rewardRepository;
    private final RewardHistoryMetricsRepository rewardHistoryMetricsRepository;
    private final EventStatusProducer eventStatusProducer;
    private final FailedEventProducer failedEventProducer;
    private final CalculatedRewardProducer calculatedRewardProducer;

    @PostConstruct
    public void init() {
        rewardCalculator = new DefaultRewardCalculator(new Expression(calculationExpressionRaw), amountCodeWord, interestRateCodeWord, bonusCodeWord);
        YearMonth yearMonth = YearMonth.now();
        OffsetDateTime curTimestamp = OffsetDateTime.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1, 0, 0, 0, 0, ZoneOffset.UTC);
        List<Reward> rewards = rewardRepository.findRewardWithMaxEventTotalAndTimestamp(curTimestamp);
        for (Reward reward : rewards) {
            currentRewardInfos.put(reward.getPointId().toString(), new RewardMetrics(
                    reward.getEventAmountTotal(), rewardRepository.sumByRewardAndTimestamp(reward.getPointId(), curTimestamp), yearMonth));
        }
    }

    @Override
    public void calcRewardFor(CalcInfoSO calcInfo) {
        String calcSchemeId;
        CalcScheme calcScheme;

        try {
            calcSchemeId = profilePointClient.getCalcSchemeId(calcInfo.pointId());

            if (calcSchemeId == null) {
                publishFailedEventMessage(calcInfo);
                return;
            }

            calcScheme = calcSchemeClient.getCalcScheme(calcSchemeId);
        } catch (Exception e) {
            publishFailedEventMessage(calcInfo);
            return;
        }

        BigDecimal currentEventAmountTotal = getCurrentEventAmountTotal(calcInfo, calcScheme);

        if (currentEventAmountTotal != null) {
            if (calcScheme.isRecalc()) {
                calcRewardWithRecalc(calcInfo, calcScheme, currentEventAmountTotal);
            } else {
                calcReward(calcInfo, calcScheme, currentEventAmountTotal);
            }
        }
    }

    @Override
    public FullRewardSO getReward(String id) {
        checkExistence(id);

        Reward reward = rewardRepository.getReferenceById(UUID.fromString(id));

        return new FullRewardSO(
                id,
                reward.getEventAmount(),
                reward.getRewardAmount(),
                reward.getRewardTime(),
                reward.getFormula(),
                reward.getProfileId().toString(),
                reward.getPointId().toString(),
                reward.getCalcSchemeId().toString()
        );
    }

    @Override
    public TotalsSO getTotals(PeriodFilterSO filter) {
        YearMonth currentPeriod = YearMonth.now(ZoneOffset.UTC);

        if (filter == null) {
            return getCurrentTotals(currentPeriod);
        }

        if (filter.periodStart() == null) {
            BigDecimal eventAmountTotal = BigDecimal.ZERO;
            BigDecimal rewardAmountTotal = BigDecimal.ZERO;

            if (filter.periodEnd().equals(currentPeriod)) {
                TotalsSO currentTotals = getCurrentTotals(currentPeriod);

                eventAmountTotal = currentTotals.eventAmountTotal();
                rewardAmountTotal = currentTotals.rewardAmountTotal();
            }

            List<RewardHistoryMetrics> metricsList = rewardHistoryMetricsRepository.getAllByPeriodLessThanEqual(filter.periodEnd());

            for (RewardHistoryMetrics metrics : metricsList) {
                eventAmountTotal = eventAmountTotal.add(metrics.getPeriodLastReward().getEventAmountTotal());
                rewardAmountTotal = rewardAmountTotal.add(metrics.getRewardAmountTotal());
            }

            return new TotalsSO(eventAmountTotal, rewardAmountTotal);
        } else if (filter.periodEnd() == null) {
            TotalsSO currentTotals = getCurrentTotals(currentPeriod);
            BigDecimal eventAmountTotal = currentTotals.eventAmountTotal();
            BigDecimal rewardAmountTotal = currentTotals.rewardAmountTotal();

            if (filter.periodStart().equals(currentPeriod)) {
                return new TotalsSO(eventAmountTotal, rewardAmountTotal);
            }

            List<RewardHistoryMetrics> metricsList = rewardHistoryMetricsRepository.getAllByPeriodGreaterThanEqual(filter.periodStart());

            for (RewardHistoryMetrics metrics : metricsList) {
                eventAmountTotal = eventAmountTotal.add(metrics.getPeriodLastReward().getEventAmountTotal());
                rewardAmountTotal = rewardAmountTotal.add(metrics.getRewardAmountTotal());
            }

            return new TotalsSO(eventAmountTotal, rewardAmountTotal);
        }

        BigDecimal eventAmountTotal = BigDecimal.ZERO;
        BigDecimal rewardAmountTotal = BigDecimal.ZERO;

        if (filter.periodEnd().equals(currentPeriod)) {
            TotalsSO currentTotals = getCurrentTotals(currentPeriod);

            eventAmountTotal = currentTotals.eventAmountTotal();
            rewardAmountTotal = currentTotals.rewardAmountTotal();

            if (filter.periodStart().equals(currentPeriod)) {
                return new TotalsSO(eventAmountTotal, rewardAmountTotal);
            }
        }

        List<RewardHistoryMetrics> metricsList = rewardHistoryMetricsRepository.getAllByPeriodGreaterThanEqualAndPeriodLessThanEqual(filter.periodStart(), filter.periodEnd());

        for (RewardHistoryMetrics metrics : metricsList) {
            eventAmountTotal = eventAmountTotal.add(metrics.getPeriodLastReward().getEventAmountTotal());
            rewardAmountTotal = rewardAmountTotal.add(metrics.getRewardAmountTotal());
        }

        return new TotalsSO(eventAmountTotal, rewardAmountTotal);
    }

    @Override
    public TotalsSO getPointTotals(String pointId, PeriodFilterSO filter) {
        YearMonth currentPeriod = YearMonth.now(ZoneOffset.UTC);

        if (filter == null) {
            return getPointCurrentTotals(pointId, currentPeriod);
        }

        if (filter.periodStart() == null) {
            // TODO: calc sum of first reward till period
            return new TotalsSO(null, null);
        } else if (filter.periodEnd() == null) {
            // TODO: calc sum of period rewards till last reward
            return new TotalsSO(null, null);
        } else if (filter.periodStart().equals(currentPeriod)) {
            return getPointCurrentTotals(pointId, currentPeriod);
        }

        // TODO: calc sum between periods

        return new TotalsSO(null, null);
    }

    @Override
    public TotalsSO getProfileTotals(String profileId, PeriodFilterSO filter) {
        List<String> pointIds = profilePointClient.getPointIdsWithProfileId(profileId);

        YearMonth currentPeriod = YearMonth.now(ZoneOffset.UTC);

        if (filter == null) {
            return getProfileCurrentTotals(pointIds, currentPeriod);
        }

        if (filter.periodStart() == null) {
            // TODO: calc sum of first reward till period
            return new TotalsSO(null, null);
        } else if (filter.periodEnd() == null) {
            // TODO: calc sum of period rewards till last reward
            return new TotalsSO(null, null);
        } else if (filter.periodStart().equals(currentPeriod)) {
            return getProfileCurrentTotals(pointIds, currentPeriod);
        }

        // TODO: calc sum between periods
        return new TotalsSO(null, null);
    }

    private BigDecimal getRewardCurrentTotal(YearMonth currentPeriod) {
        BigDecimal rewardAmountTotal = BigDecimal.ZERO;

        for (Map.Entry<String, RewardMetrics> entry : currentRewardInfos.entrySet()) {
            RewardMetrics rewardMetrics = entry.getValue();

            if (rewardMetrics.accountingPeriod.compareTo(currentPeriod) < 0) {
                saveHistoryAndResetCurrentMetrics(entry.getKey(), rewardMetrics, currentPeriod);
            }

            rewardAmountTotal = rewardAmountTotal.add(rewardMetrics.rewardAmountTotal);
        }

        return rewardAmountTotal;
    }

    private BigDecimal getProfileRewardCurrentTotal(List<String> pointIds, YearMonth currentPeriod) {
        BigDecimal rewardAmountTotal = BigDecimal.ZERO;

        for (String pointId : pointIds) {
            RewardMetrics rewardMetrics = currentRewardInfos.getOrDefault(
                    pointId,
                    new RewardMetrics(BigDecimal.ZERO, BigDecimal.ZERO, currentPeriod)
            );

            if (rewardMetrics.accountingPeriod.compareTo(currentPeriod) < 0) {
                saveHistoryAndResetCurrentMetrics(pointId, rewardMetrics, currentPeriod);
            }

            rewardAmountTotal = rewardAmountTotal.add(rewardMetrics.rewardAmountTotal);
        }

        return rewardAmountTotal;
    }

    private BigDecimal getPointRewardCurrentTotal(String pointId, YearMonth currentPeriod) {
        RewardMetrics rewardMetrics = currentRewardInfos.getOrDefault(
                pointId,
                new RewardMetrics(BigDecimal.ZERO, BigDecimal.ZERO, currentPeriod)
        );

        if (rewardMetrics.accountingPeriod.compareTo(currentPeriod) < 0) {
            saveHistoryAndResetCurrentMetrics(pointId, rewardMetrics, currentPeriod);
        }

        return rewardMetrics.rewardAmountTotal;
    }

    private TotalsSO getCurrentTotals(YearMonth currentPeriod) {
        BigDecimal eventAmountTotal = BigDecimal.ZERO;
        BigDecimal rewardAmountTotal = BigDecimal.ZERO;

        for (Map.Entry<String, RewardMetrics> entry : currentRewardInfos.entrySet()) {
            RewardMetrics rewardMetrics = entry.getValue();

            if (rewardMetrics.accountingPeriod.compareTo(currentPeriod) < 0) {
                saveHistoryAndResetCurrentMetrics(entry.getKey(), rewardMetrics, currentPeriod);
            }

            eventAmountTotal = eventAmountTotal.add(rewardMetrics.eventAmountTotal);
            rewardAmountTotal = rewardAmountTotal.add(rewardMetrics.rewardAmountTotal);
        }

        return new TotalsSO(eventAmountTotal, rewardAmountTotal);
    }

    private TotalsSO getProfileCurrentTotals(List<String> pointIds, YearMonth currentPeriod) {
        BigDecimal eventAmountTotal = BigDecimal.ZERO;
        BigDecimal rewardAmountTotal = BigDecimal.ZERO;

        for (String pointId : pointIds) {
            RewardMetrics rewardMetrics = currentRewardInfos.getOrDefault(
                    pointId,
                    new RewardMetrics(BigDecimal.ZERO, BigDecimal.ZERO, currentPeriod)
            );

            if (rewardMetrics.accountingPeriod.compareTo(currentPeriod) < 0) {
                saveHistoryAndResetCurrentMetrics(pointId, rewardMetrics, currentPeriod);
            }

            eventAmountTotal = eventAmountTotal.add(rewardMetrics.eventAmountTotal);
            rewardAmountTotal = rewardAmountTotal.add(rewardMetrics.rewardAmountTotal);
        }

        return new TotalsSO(eventAmountTotal, rewardAmountTotal);
    }

    private TotalsSO getPointCurrentTotals(String pointId, YearMonth currentPeriod) {
        RewardMetrics rewardMetrics = currentRewardInfos.getOrDefault(
                pointId,
                new RewardMetrics(BigDecimal.ZERO, BigDecimal.ZERO, currentPeriod)
        );

        if (rewardMetrics.accountingPeriod.compareTo(currentPeriod) < 0) {
            saveHistoryAndResetCurrentMetrics(pointId, rewardMetrics, currentPeriod);
        }

        return new TotalsSO(rewardMetrics.eventAmountTotal, rewardMetrics.rewardAmountTotal);
    }

    @Override
    public List<HistoryTotalsSO> getHistoryTotals(PeriodFilterSO filter) {
        return null;
    }

    @Override
    public List<HistoryTotalsSO> getPointHistoryTotals(String pointId, PeriodFilterSO filter) {
        YearMonth currentPeriod = YearMonth.now(ZoneOffset.UTC);

        RewardMetrics rewardMetrics = currentRewardInfos.getOrDefault(
                pointId,
                new RewardMetrics(BigDecimal.ZERO, BigDecimal.ZERO, currentPeriod)
        );

        if (filter.periodStart() == null) {
            List<RewardHistoryMetrics> historyTotals = rewardHistoryMetricsRepository
                    .getRewardHistoryMetricsByPointIdAndPeriodLessThanEqual(UUID.fromString(pointId), filter.periodEnd());

            if (filter.periodEnd().equals(currentPeriod)) {
                HistoryTotalsSO currentTotals = getCurrentHistoryTotals(pointId, rewardMetrics, currentPeriod);

                return Stream.concat(
                        historyTotals.stream().map(historyTotal -> new HistoryTotalsSO(
                                historyTotal.getPeriodLastReward().getEventAmountTotal(),
                                historyTotal.getRewardAmountTotal(),
                                historyTotal.getPeriod())),
                        Stream.of(currentTotals)
                ).toList();
            }

            return historyTotals.stream().map(historyTotal -> new HistoryTotalsSO(
                    historyTotal.getPeriodLastReward().getEventAmountTotal(),
                    historyTotal.getRewardAmountTotal(),
                    historyTotal.getPeriod())).toList();
        } else if (filter.periodEnd() == null) {
            HistoryTotalsSO currentTotals = getCurrentHistoryTotals(pointId, rewardMetrics, currentPeriod);

            if (filter.periodStart().equals(currentPeriod)) {
                return List.of(currentTotals);
            }

            List<RewardHistoryMetrics> historyTotals = rewardHistoryMetricsRepository
                    .getRewardHistoryMetricsByPointIdAndPeriodGreaterThanEqual(UUID.fromString(pointId), filter.periodStart());

            return Stream.concat(
                    historyTotals.stream().map(historyTotal -> new HistoryTotalsSO(
                            historyTotal.getPeriodLastReward().getEventAmountTotal(),
                            historyTotal.getRewardAmountTotal(),
                            historyTotal.getPeriod())),
                    Stream.of(currentTotals)
            ).toList();
        }

        HistoryTotalsSO currentTotals = getCurrentHistoryTotals(pointId, rewardMetrics, currentPeriod);

        if (filter.periodStart().equals(currentPeriod)) {
            return List.of(currentTotals);
        }

        List<RewardHistoryMetrics> historyTotals = rewardHistoryMetricsRepository
                .getRewardHistoryMetricsByPointIdAndPeriodGreaterThanEqualAndPeriodLessThanEqual(UUID.fromString(pointId), filter.periodStart(), filter.periodEnd());

        return Stream.concat(
                historyTotals.stream().map(historyTotal -> new HistoryTotalsSO(
                        historyTotal.getPeriodLastReward().getEventAmountTotal(),
                        historyTotal.getRewardAmountTotal(),
                        historyTotal.getPeriod())),
                Stream.of(currentTotals)
        ).toList();
    }

    @Override
    public List<HistoryTotalsSO> getProfileHistoryTotals(String profileId, PeriodFilterSO filter) {
        return null;
    }

    @Override
    public BigDecimal findAvgReward(PeriodFilterSO filter) {
        YearMonth currentPeriod = YearMonth.now(ZoneOffset.UTC);

        if (filter == null) {
            BigDecimal rewardAmountTotal = getRewardCurrentTotal(currentPeriod);
            return rewardAmountTotal.divide(BigDecimal.valueOf(currentRewardInfos.size()), RoundingMode.HALF_EVEN);
        }

        if (filter.periodStart() == null) {
            // TODO: calc avg of first reward till period
            return null;
        } else if (filter.periodEnd() == null) {
            // TODO: calc avg of period rewards till last reward
            return null;
        } else if (filter.periodStart().equals(currentPeriod)) {
            BigDecimal rewardAmountTotal = getRewardCurrentTotal(currentPeriod);
            return rewardAmountTotal.divide(BigDecimal.valueOf(currentRewardInfos.size()), RoundingMode.HALF_EVEN);
        }

        // TODO: calc avg between periods
        return null;
    }

    @Override
    public BigDecimal findProfileAvgReward(String profileId, PeriodFilterSO filter) {
        List<String> pointIds = profilePointClient.getPointIdsWithProfileId(profileId);

        YearMonth currentPeriod = YearMonth.now(ZoneOffset.UTC);

        if (filter == null) {
            BigDecimal rewardAmountTotal = getProfileRewardCurrentTotal(pointIds, currentPeriod);
            return currentRewardInfos.size() == 0 ? BigDecimal.ZERO : rewardAmountTotal.divide(BigDecimal.valueOf(currentRewardInfos.size()), RoundingMode.HALF_EVEN);
        }

        if (filter.periodStart() == null) {
            BigDecimal rewardAmountTotal = BigDecimal.ZERO;
            int recordsNum = 0;

            for (String pointId : pointIds) {
                List<RewardHistoryMetrics> metricsList = rewardHistoryMetricsRepository.
                        getRewardHistoryMetricsByPointIdAndPeriodLessThanEqual(UUID.fromString(pointId), filter.periodEnd());

                for (RewardHistoryMetrics metrics : metricsList) {
                    rewardAmountTotal = rewardAmountTotal.add(metrics.getRewardAmountTotal());
                }

                recordsNum += metricsList.size();
            }

            return recordsNum == 0 ? BigDecimal.ZERO : rewardAmountTotal.divide(BigDecimal.valueOf(recordsNum), RoundingMode.HALF_EVEN);
        } else if (filter.periodEnd() == null) {
            BigDecimal rewardAmountTotal = BigDecimal.ZERO;
            int recordsNum = 0;

            for (String pointId : pointIds) {
                List<RewardHistoryMetrics> metricsList = rewardHistoryMetricsRepository.
                        getRewardHistoryMetricsByPointIdAndPeriodGreaterThanEqual(UUID.fromString(pointId), filter.periodStart());

                for (RewardHistoryMetrics metrics : metricsList) {
                    rewardAmountTotal = rewardAmountTotal.add(metrics.getRewardAmountTotal());
                }

                recordsNum += metricsList.size();
            }

            return rewardAmountTotal.divide(BigDecimal.valueOf(recordsNum), RoundingMode.HALF_EVEN);
        } else if (filter.periodStart().equals(currentPeriod)) {
            BigDecimal rewardAmountTotal = getProfileRewardCurrentTotal(pointIds, currentPeriod);
            return currentRewardInfos.size() == 0 ? BigDecimal.ZERO : rewardAmountTotal.divide(BigDecimal.valueOf(currentRewardInfos.size()), RoundingMode.HALF_EVEN);
        }

        BigDecimal rewardAmountTotal = BigDecimal.ZERO;
        int recordsNum = 0;

        for (String pointId : pointIds) {
            List<RewardHistoryMetrics> metricsList = rewardHistoryMetricsRepository.
                    getRewardHistoryMetricsByPointIdAndPeriodGreaterThanEqualAndPeriodLessThanEqual(UUID.fromString(pointId),
                            filter.periodStart(), filter.periodEnd());

            for (RewardHistoryMetrics metrics : metricsList) {
                rewardAmountTotal = rewardAmountTotal.add(metrics.getRewardAmountTotal());
            }

            recordsNum += metricsList.size();
        }

        if (filter.periodEnd().equals(currentPeriod)) {
            rewardAmountTotal = rewardAmountTotal.add(getProfileRewardCurrentTotal(pointIds, currentPeriod));
            recordsNum++;
        }

        return recordsNum == 0 ? BigDecimal.ZERO : rewardAmountTotal.divide(BigDecimal.valueOf(recordsNum), RoundingMode.HALF_EVEN);
    }

    @Override
    public BigDecimal findPointAvgReward(String pointId, PeriodFilterSO filter) {
        YearMonth currentPeriod = YearMonth.now(ZoneOffset.UTC);

        if (filter == null) {
            BigDecimal rewardAmountTotal = getPointRewardCurrentTotal(pointId, currentPeriod);
            return currentRewardInfos.size() == 0 ? BigDecimal.ZERO : rewardAmountTotal.divide(BigDecimal.valueOf(currentRewardInfos.size()), RoundingMode.HALF_EVEN);
        }

        if (filter.periodStart() == null) {
            List<RewardHistoryMetrics> metricsList = rewardHistoryMetricsRepository.
                    getRewardHistoryMetricsByPointIdAndPeriodLessThanEqual(UUID.fromString(pointId), filter.periodEnd());

            BigDecimal rewardAmountTotal = BigDecimal.ZERO;
            int recordsNum = 0;

            for (RewardHistoryMetrics metrics : metricsList) {
                rewardAmountTotal = rewardAmountTotal.add(metrics.getRewardAmountTotal());
            }

            recordsNum += metricsList.size();

            if (filter.periodEnd().equals(currentPeriod)) {
                rewardAmountTotal = rewardAmountTotal.add(getPointRewardCurrentTotal(pointId, currentPeriod));
                recordsNum++;
            }

            return recordsNum == 0 ? BigDecimal.ZERO : rewardAmountTotal.divide(BigDecimal.valueOf(recordsNum), RoundingMode.HALF_EVEN);
        } else if (filter.periodEnd() == null) {
            List<RewardHistoryMetrics> metricsList = rewardHistoryMetricsRepository.
                    getRewardHistoryMetricsByPointIdAndPeriodGreaterThanEqual(UUID.fromString(pointId), filter.periodStart());

            BigDecimal rewardAmountTotal = BigDecimal.ZERO;
            int recordsNum = 0;

            for (RewardHistoryMetrics metrics : metricsList) {
                rewardAmountTotal = rewardAmountTotal.add(metrics.getRewardAmountTotal());
            }

            recordsNum += metricsList.size();

            if (filter.periodEnd().equals(currentPeriod)) {
                rewardAmountTotal = rewardAmountTotal.add(getPointRewardCurrentTotal(pointId, currentPeriod));
                recordsNum++;
            }

            return recordsNum == 0 ? BigDecimal.ZERO : rewardAmountTotal.divide(BigDecimal.valueOf(recordsNum), RoundingMode.HALF_EVEN);
        } else if (filter.periodStart().equals(currentPeriod)) {
            BigDecimal rewardAmountTotal = getPointRewardCurrentTotal(pointId, currentPeriod);
            return currentRewardInfos.size() == 0 ? BigDecimal.ZERO : rewardAmountTotal.divide(BigDecimal.valueOf(currentRewardInfos.size()), RoundingMode.HALF_EVEN);
        }

        List<RewardHistoryMetrics> metricsList = rewardHistoryMetricsRepository.
                getRewardHistoryMetricsByPointIdAndPeriodGreaterThanEqualAndPeriodLessThanEqual(UUID.fromString(pointId), filter.periodStart(), filter.periodEnd());

        BigDecimal rewardAmountTotal = BigDecimal.ZERO;
        int recordsNum = 0;

        for (RewardHistoryMetrics metrics : metricsList) {
            rewardAmountTotal = rewardAmountTotal.add(metrics.getRewardAmountTotal());
        }

        recordsNum += metricsList.size();

        if (filter.periodEnd().equals(currentPeriod)) {
            rewardAmountTotal = rewardAmountTotal.add(getPointRewardCurrentTotal(pointId, currentPeriod));
            recordsNum++;
        }

        return recordsNum == 0 ? BigDecimal.ZERO : rewardAmountTotal.divide(BigDecimal.valueOf(recordsNum), RoundingMode.HALF_EVEN);
    }

    @Override
    public MaxRewardSO findProfileMaxReward(String profileId, PeriodFilterSO filter) {
        return getProfileMaxReward(profileId, filter);
    }

    @Override
    public MinRewardSO findProfileMinReward(String profileId, PeriodFilterSO filter) {
        return getProfileMinReward(profileId, filter);
    }

    @Override
    public MaxRewardSO findPointMaxReward(String pointId, PeriodFilterSO filter) {
        return getPointMaxReward(pointId, filter);
    }

    @Override
    public MinRewardSO findPointMinReward(String pointId, PeriodFilterSO filter) {
        return getPointMinReward(pointId, filter);
    }

    @Override
    public ExtremeRewardSO findExtremeReward(PeriodFilterSO filter) {
        // TODO: implement extreme among all profiles and points
        return new ExtremeRewardSO(new MaxRewardSO(null, null, null), new MinRewardSO(null, null, null));
    }

    @Override
    public ExtremeRewardSO findProfileExtremeReward(String profileId, PeriodFilterSO filter) {
        return new ExtremeRewardSO(getProfileMaxReward(profileId, filter), getProfileMinReward(profileId, filter));
    }

    @Override
    public ExtremeRewardSO findPointExtremeReward(String pointId, PeriodFilterSO filter) {
        return new ExtremeRewardSO(getPointMaxReward(pointId, filter), getPointMinReward(pointId, filter));
    }

    private MaxRewardSO getProfileMaxReward(String profileId, PeriodFilterSO filter) {
        List<String> pointIds = profilePointClient.getPointIdsWithProfileId(profileId);
        Reward max = null;

        if (filter.periodStart() == null) {
            LocalDate endDay = filter.periodEnd().atEndOfMonth();
            OffsetDateTime time = OffsetDateTime.of(filter.periodEnd().getYear(), filter.periodEnd().getMonthValue(), endDay.getDayOfMonth(), 23, 59, 59, 999999999, ZoneOffset.UTC);

            for (String pointId : pointIds) {
                if (max == null) {
                    max = rewardRepository.findFirstByPointIdAndRewardTimeLessThanEqualOrderByRewardAmountDesc(UUID.fromString(pointId), time);
                } else {
                    Reward curMax = rewardRepository.findFirstByPointIdAndRewardTimeLessThanEqualOrderByRewardAmountDesc(UUID.fromString(pointId), time);

                    if (max.getRewardAmount().compareTo(curMax.getRewardAmount()) < 0) {
                        max = curMax;
                    }
                }
            }

            return new MaxRewardSO(max.getPointId().toString(), max.getRewardAmount(), YearMonth.of(max.getRewardTime().getYear(), max.getRewardTime().getMonth()));
        } else if (filter.periodEnd() == null) {
            OffsetDateTime time = OffsetDateTime.of(filter.periodStart().getYear(), filter.periodStart().getMonthValue(), 1, 0, 0, 0, 0, ZoneOffset.UTC);

            for (String pointId : pointIds) {
                if (max == null) {
                    max = rewardRepository.findFirstByPointIdAndRewardTimeGreaterThanEqualOrderByRewardAmountDesc(UUID.fromString(pointId), time);
                } else {
                    Reward curMax = rewardRepository.findFirstByPointIdAndRewardTimeGreaterThanEqualOrderByRewardAmountDesc(UUID.fromString(pointId), time);

                    if (max.getRewardAmount().compareTo(curMax.getRewardAmount()) < 0) {
                        max = curMax;
                    }
                }
            }

            return new MaxRewardSO(max.getPointId().toString(), max.getRewardAmount(), YearMonth.of(max.getRewardTime().getYear(), max.getRewardTime().getMonth()));
        }

        LocalDate endDay = filter.periodEnd().atEndOfMonth();

        OffsetDateTime start = OffsetDateTime.of(filter.periodStart().getYear(), filter.periodStart().getMonthValue(), 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime end = OffsetDateTime.of(filter.periodEnd().getYear(), filter.periodEnd().getMonthValue(), endDay.getDayOfMonth(), 23, 59, 59, 999999999, ZoneOffset.UTC);

        for (String pointId : pointIds) {
            if (max == null) {
                max = rewardRepository.findFirstByPointIdAndRewardTimeGreaterThanEqualAndRewardTimeLessThanEqualOrderByRewardAmountDesc(UUID.fromString(pointId), start, end);
            } else {
                Reward curMax = rewardRepository.findFirstByPointIdAndRewardTimeGreaterThanEqualAndRewardTimeLessThanEqualOrderByRewardAmountDesc(UUID.fromString(pointId), start, end);

                if (max.getRewardAmount().compareTo(curMax.getRewardAmount()) < 0) {
                    max = curMax;
                }
            }
        }

        return new MaxRewardSO(max.getPointId().toString(), max.getRewardAmount(), YearMonth.of(max.getRewardTime().getYear(), max.getRewardTime().getMonth()));
    }

    private MinRewardSO getProfileMinReward(String profileId, PeriodFilterSO filter) {
        List<String> pointIds = profilePointClient.getPointIdsWithProfileId(profileId);
        Reward min = null;

        if (filter.periodStart() == null) {
            LocalDate endDay = filter.periodEnd().atEndOfMonth();
            OffsetDateTime time = OffsetDateTime.of(filter.periodEnd().getYear(), filter.periodEnd().getMonthValue(), endDay.getDayOfMonth(), 23, 59, 59, 999999999, ZoneOffset.UTC);

            for (String pointId : pointIds) {
                if (min == null) {
                    min = rewardRepository.findFirstByPointIdAndRewardTimeLessThanEqualOrderByRewardAmountAsc(UUID.fromString(pointId), time);
                } else {
                    Reward curMin = rewardRepository.findFirstByPointIdAndRewardTimeLessThanEqualOrderByRewardAmountAsc(UUID.fromString(pointId), time);

                    if (min.getRewardAmount().compareTo(curMin.getRewardAmount()) > 0) {
                        min = curMin;
                    }
                }
            }

            return new MinRewardSO(min.getPointId().toString(), min.getRewardAmount(), YearMonth.of(min.getRewardTime().getYear(), min.getRewardTime().getMonth()));
        } else if (filter.periodEnd() == null) {
            OffsetDateTime time = OffsetDateTime.of(filter.periodStart().getYear(), filter.periodStart().getMonthValue(), 1, 0, 0, 0, 0, ZoneOffset.UTC);

            for (String pointId : pointIds) {
                if (min == null) {
                    min = rewardRepository.findFirstByPointIdAndRewardTimeGreaterThanEqualOrderByRewardAmountAsc(UUID.fromString(pointId), time);
                } else {
                    Reward curMin = rewardRepository.findFirstByPointIdAndRewardTimeGreaterThanEqualOrderByRewardAmountAsc(UUID.fromString(pointId), time);

                    if (min.getRewardAmount().compareTo(curMin.getRewardAmount()) > 0) {
                        min = curMin;
                    }
                }
            }

            return new MinRewardSO(min.getPointId().toString(), min.getRewardAmount(), YearMonth.of(min.getRewardTime().getYear(), min.getRewardTime().getMonth()));
        }

        LocalDate endDay = filter.periodEnd().atEndOfMonth();

        OffsetDateTime start = OffsetDateTime.of(filter.periodStart().getYear(), filter.periodStart().getMonthValue(), 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime end = OffsetDateTime.of(filter.periodEnd().getYear(), filter.periodEnd().getMonthValue(), endDay.getDayOfMonth(), 23, 59, 59, 999999999, ZoneOffset.UTC);

        for (String pointId : pointIds) {
            if (min == null) {
                min = rewardRepository.findFirstByPointIdAndRewardTimeGreaterThanEqualAndRewardTimeLessThanEqualOrderByRewardAmountAsc(UUID.fromString(pointId), start, end);
            } else {
                Reward curMin = rewardRepository.findFirstByPointIdAndRewardTimeGreaterThanEqualAndRewardTimeLessThanEqualOrderByRewardAmountAsc(UUID.fromString(pointId), start, end);

                if (min.getRewardAmount().compareTo(curMin.getRewardAmount()) > 0) {
                    min = curMin;
                }
            }
        }

        return new MinRewardSO(min.getPointId().toString(), min.getRewardAmount(), YearMonth.of(min.getRewardTime().getYear(), min.getRewardTime().getMonth()));
    }

    private MinRewardSO getPointMinReward(String pointId, PeriodFilterSO filter) {
        if (filter.periodStart() == null) {
            LocalDate endDay = filter.periodEnd().atEndOfMonth();

            OffsetDateTime time = OffsetDateTime.of(filter.periodEnd().getYear(), filter.periodEnd().getMonthValue(), endDay.getDayOfMonth(), 23, 59, 59, 999999999, ZoneOffset.UTC);

            Reward min = rewardRepository.findFirstByPointIdAndRewardTimeLessThanEqualOrderByRewardAmountAsc(UUID.fromString(pointId), time);

            return new MinRewardSO(pointId, min.getRewardAmount(), YearMonth.of(min.getRewardTime().getYear(), min.getRewardTime().getMonth()));
        } else if (filter.periodEnd() == null) {
            OffsetDateTime time = OffsetDateTime.of(filter.periodStart().getYear(), filter.periodStart().getMonthValue(), 1, 0, 0, 0, 0, ZoneOffset.UTC);

            Reward min = rewardRepository.findFirstByPointIdAndRewardTimeGreaterThanEqualOrderByRewardAmountAsc(UUID.fromString(pointId), time);

            return new MinRewardSO(pointId, min.getRewardAmount(), YearMonth.of(min.getRewardTime().getYear(), min.getRewardTime().getMonth()));
        }

        LocalDate endDay = filter.periodEnd().atEndOfMonth();

        OffsetDateTime start = OffsetDateTime.of(filter.periodStart().getYear(), filter.periodStart().getMonthValue(), 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime end = OffsetDateTime.of(filter.periodEnd().getYear(), filter.periodEnd().getMonthValue(), endDay.getDayOfMonth(), 23, 59, 59, 999999999, ZoneOffset.UTC);

        Reward min = rewardRepository.findFirstByPointIdAndRewardTimeGreaterThanEqualAndRewardTimeLessThanEqualOrderByRewardAmountAsc(UUID.fromString(pointId), start, end);

        return new MinRewardSO(pointId, min.getRewardAmount(), YearMonth.of(min.getRewardTime().getYear(), min.getRewardTime().getMonth()));
    }

    private MaxRewardSO getPointMaxReward(String pointId, PeriodFilterSO filter) {
        if (filter.periodStart() == null) {
            LocalDate endDay = filter.periodEnd().atEndOfMonth();

            OffsetDateTime time = OffsetDateTime.of(filter.periodEnd().getYear(), filter.periodEnd().getMonthValue(), endDay.getDayOfMonth(), 23, 59, 59, 999999999, ZoneOffset.UTC);

            Reward max = rewardRepository.findFirstByPointIdAndRewardTimeLessThanEqualOrderByRewardAmountDesc(UUID.fromString(pointId), time);

            return new MaxRewardSO(pointId, max.getRewardAmount(), YearMonth.of(max.getRewardTime().getYear(), max.getRewardTime().getMonth()));
        } else if (filter.periodEnd() == null) {
            OffsetDateTime time = OffsetDateTime.of(filter.periodStart().getYear(), filter.periodStart().getMonthValue(), 1, 0, 0, 0, 0, ZoneOffset.UTC);

            Reward max = rewardRepository.findFirstByPointIdAndRewardTimeGreaterThanEqualOrderByRewardAmountDesc(UUID.fromString(pointId), time);

            return new MaxRewardSO(pointId, max.getRewardAmount(), YearMonth.of(max.getRewardTime().getYear(), max.getRewardTime().getMonth()));
        }

        LocalDate endDay = filter.periodEnd().atEndOfMonth();

        OffsetDateTime start = OffsetDateTime.of(filter.periodStart().getYear(), filter.periodStart().getMonthValue(), 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime end = OffsetDateTime.of(filter.periodEnd().getYear(), filter.periodEnd().getMonthValue(), endDay.getDayOfMonth(), 23, 59, 59, 999999999, ZoneOffset.UTC);

        Reward max = rewardRepository.findFirstByPointIdAndRewardTimeGreaterThanEqualAndRewardTimeLessThanEqualOrderByRewardAmountDesc(UUID.fromString(pointId), start, end);

        return new MaxRewardSO(pointId, max.getRewardAmount(), YearMonth.of(max.getRewardTime().getYear(), max.getRewardTime().getMonth()));
    }

    private HistoryTotalsSO getCurrentHistoryTotals(String pointId, RewardMetrics rewardMetrics, YearMonth currentPeriod) {
        if (rewardMetrics.accountingPeriod.compareTo(currentPeriod) < 0) {
            saveHistoryAndResetCurrentMetrics(pointId, rewardMetrics, currentPeriod);
        }

        return new HistoryTotalsSO(rewardMetrics.eventAmountTotal, rewardMetrics.rewardAmountTotal, currentPeriod);
    }

    private void publishFailedEventMessage(CalcInfoSO failedEvent) {
        failedEventProducer.sendMessage(
                FailedCalcInfo.FailedCalcInfoMessage.newBuilder()
                        .setCalcInfo(CalcInfo.CalcInfoMessage.newBuilder()
                                        .setEventId(failedEvent.eventId())
                                        .setAmount(BigDecimalUtil.toProtoDecimalValue(failedEvent.amount()))
                                        .setProfileId(failedEvent.profileId())
                                        .setPointId(failedEvent.pointId())
                                        .build())
                        .setFailsNum(1)
                        .build()
        );
    }

    private void checkExistence(String id) {
        if (!rewardRepository.existsById(UUID.fromString(id))) {
            log.debug("Reward with eventId " + id + " was not found");
            throw new HttpStatusCodeException(
                    new NotFoundErrorCause(List.of(HttpErrorCause.NotFound.REWARD_NOT_FOUND)),
                    id
            );
        }
        log.debug("Reward with eventId " + id + " was successfully found");
    }

    private void calcReward(CalcInfoSO calcInfo, CalcScheme calcScheme, BigDecimal currentEventAmountTotal) {
        RewardCalculator.RewardWithFormula rewardWithFormula = rewardCalculator.calcReward(
                calcInfo.amount(),
                currentEventAmountTotal,
                calcScheme.calcRules()
        );
        afterCalcSaveAndUpdate(calcInfo, rewardWithFormula.reward(), rewardWithFormula.formula(), calcScheme.id());
    }

    private BigDecimal getCurrentEventAmountTotal(CalcInfoSO calcInfo, CalcScheme calcScheme) {
        UUID eventId = UUID.fromString(calcInfo.eventId());
        BigDecimal currentEventAmountTotal;

        if (rewardRepository.existsById(eventId)) {
            Reward reward = rewardRepository.getReferenceById(eventId);
            if (reward.getCalcSchemeId().toString().equals(calcScheme.id()) &&
                    reward.getEventAmount().equals(calcInfo.amount().setScale(6, RoundingMode.HALF_EVEN))) {
                return null;
            }
            currentEventAmountTotal = reward.getEventAmountTotal().subtract(reward.getEventAmount()).setScale(0, RoundingMode.HALF_UP);
            currentRewardInfos.compute(calcInfo.pointId(), (key, value) -> {
                if (value == null) {
                    value = new RewardMetrics(currentEventAmountTotal, value.rewardAmountTotal
                            .subtract(reward.getRewardAmount())
                            .setScale(0, RoundingMode.HALF_UP), YearMonth.now());
                } else {
                    value.eventAmountTotal = currentEventAmountTotal;
                    value.rewardAmountTotal = value.rewardAmountTotal
                            .subtract(reward.getRewardAmount())
                            .setScale(0, RoundingMode.HALF_UP);
                }
                return value;
            });
        } else {
            RewardMetrics currentRewardMetrics = currentRewardInfos.get(calcInfo.pointId());
            YearMonth currentYearMonth = YearMonth.now(ZoneOffset.UTC);

            if (currentRewardMetrics == null) {
                currentRewardInfos.put(calcInfo.pointId(), new RewardMetrics(BigDecimal.ZERO, BigDecimal.ZERO, currentYearMonth));
                return BigDecimal.ZERO;
            } else if (currentRewardMetrics.accountingPeriod.compareTo(currentYearMonth) < 0) {
                saveHistoryAndResetCurrentMetrics(calcInfo.pointId(), currentRewardMetrics, currentYearMonth);
                return BigDecimal.ZERO;
            }

            currentEventAmountTotal = currentRewardMetrics.eventAmountTotal;
        }

        return currentEventAmountTotal;
    }

    private void calcRewardWithRecalc(CalcInfoSO calcInfo, CalcScheme calcScheme, BigDecimal currentEventAmountTotal) {
        RewardCalculator.RecalcRewardWithFormula rewardWithFormula = rewardCalculator.calcRewardWithRecalc(
                calcInfo.amount(),
                currentEventAmountTotal,
                calcScheme.calcRules()
        );

        if (rewardWithFormula.crossedRuleNumber().isPresent() && rewardWithFormula.crossedRuleNumber().get() > 0) {
            List<Reward> pastRewards = rewardRepository.getRewardsByPointId(UUID.fromString(calcInfo.pointId()));

            RewardMetrics currentMetrics = currentRewardInfos.get(calcInfo.pointId());
            currentMetrics.rewardAmountTotal = BigDecimal.ZERO;

            for (Reward pastReward : pastRewards) {
                RewardCalculator.RewardWithFormula recalculatedPastReward = rewardCalculator.calcReward(
                        pastReward.getEventAmount(),
                        calcScheme.calcRules().get(rewardWithFormula.crossedRuleNumber().get()).interestRate()
                );
                afterCalcSaveAndUpdate(pastReward, recalculatedPastReward.reward(), recalculatedPastReward.formula());
                currentMetrics.rewardAmountTotal = currentMetrics.rewardAmountTotal.add(recalculatedPastReward.reward());
            }

            currentRewardInfos.put(calcInfo.pointId(), currentMetrics);
        }

        afterCalcSaveAndUpdate(calcInfo,
                rewardWithFormula.rewardWithFormula().reward(),
                rewardWithFormula.rewardWithFormula().formula(),
                calcScheme.id()
        );
    }

    private void afterCalcSaveAndUpdate(Reward rewardObject, BigDecimal rewardValue, String formula) {
        rewardObject.setRewardAmount(rewardValue);
        rewardObject.setRewardTime(OffsetDateTime.now());
        rewardObject.setFormula(formula);
        rewardRepository.save(rewardObject);

        eventStatusProducer.sendMessage(EventStatus.EventStatusMessage.newBuilder()
                .setEventId(rewardObject.getEventId().toString())
                .setEventStatus(EventStatus.Status.PROCESSED)
                .build());

        calculatedRewardProducer.sendMessage(CalculatedReward.RewardMessage.newBuilder()
                .setEventId(rewardObject.getEventId().toString())
                .setPointId(rewardObject.getPointId().toString())
                .setEventAmount(BigDecimalUtil.toProtoDecimalValue(rewardObject.getEventAmount()))
                .setRewardAmount(BigDecimalUtil.toProtoDecimalValue(rewardValue))
                .build());
    }

    private void afterCalcSaveAndUpdate(CalcInfoSO calcInfo, BigDecimal reward, String formula, String calcSchemeId) {
        RewardMetrics currentMetrics = currentRewardInfos.get(calcInfo.pointId());
        currentMetrics.rewardAmountTotal = currentMetrics.rewardAmountTotal.add(reward);
        currentMetrics.eventAmountTotal = currentMetrics.eventAmountTotal.add(calcInfo.amount());
        currentRewardInfos.put(calcInfo.pointId(), currentMetrics);

        rewardRepository.save(
                new Reward(
                        UUID.fromString(calcInfo.eventId()), calcInfo.amount(), reward, currentMetrics.eventAmountTotal,
                        formula, OffsetDateTime.now(), UUID.fromString(calcInfo.profileId()),
                        UUID.fromString(calcInfo.pointId()), UUID.fromString(calcSchemeId)
                )
        );

        eventStatusProducer.sendMessage(EventStatus.EventStatusMessage.newBuilder()
                .setEventId(calcInfo.eventId())
                .setEventStatus(EventStatus.Status.PROCESSED)
                .build());

        calculatedRewardProducer.sendMessage(CalculatedReward.RewardMessage.newBuilder()
                .setEventId(calcInfo.eventId())
                .setPointId(calcInfo.pointId())
                .setEventAmount(BigDecimalUtil.toProtoDecimalValue(calcInfo.amount()))
                .setRewardAmount(BigDecimalUtil.toProtoDecimalValue(reward))
                .build());
    }

    private void saveHistoryAndResetCurrentMetrics(String pointId, RewardMetrics rewardMetrics, YearMonth currentYearMonth) {
        Reward lastReward = rewardRepository.findTopByOrderByRewardTimeDesc();

        rewardHistoryMetricsRepository.save(new RewardHistoryMetrics(
                UUID.fromString(pointId),
                lastReward,
                rewardMetrics.rewardAmountTotal,
                rewardMetrics.accountingPeriod
        ));

        rewardMetrics.accountingPeriod = currentYearMonth;
        rewardMetrics.eventAmountTotal = BigDecimal.ZERO;
        rewardMetrics.rewardAmountTotal = BigDecimal.ZERO;

        currentRewardInfos.put(pointId, rewardMetrics);
    }

    @AllArgsConstructor
    private static class RewardMetrics {
        private BigDecimal eventAmountTotal;
        private BigDecimal rewardAmountTotal;
        private YearMonth accountingPeriod;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RewardMetrics that)) return false;
            return Objects.equals(accountingPeriod, that.accountingPeriod) && Objects.equals(eventAmountTotal, that.eventAmountTotal) && Objects.equals(rewardAmountTotal, that.rewardAmountTotal);
        }

        @Override
        public int hashCode() {
            return Objects.hash(accountingPeriod, eventAmountTotal, rewardAmountTotal);
        }
    }
}
