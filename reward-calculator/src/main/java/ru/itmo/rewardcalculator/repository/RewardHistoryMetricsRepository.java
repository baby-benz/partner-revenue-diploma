package ru.itmo.rewardcalculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.itmo.rewardcalculator.domain.entity.RewardHistoryMetrics;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public interface RewardHistoryMetricsRepository extends JpaRepository<RewardHistoryMetrics, UUID>, JpaSpecificationExecutor<RewardHistoryMetrics> {
    List<RewardHistoryMetrics> getAllByPeriodGreaterThanEqual(YearMonth periodStart);
    List<RewardHistoryMetrics> getAllByPeriodLessThanEqual(YearMonth periodEnd);
    List<RewardHistoryMetrics> getAllByPeriodGreaterThanEqualAndPeriodLessThanEqual(YearMonth periodStart, YearMonth periodEnd);
    List<RewardHistoryMetrics> getRewardHistoryMetricsByPointIdAndPeriodGreaterThanEqual(UUID pointId, YearMonth periodStart);
    List<RewardHistoryMetrics> getRewardHistoryMetricsByPointIdAndPeriodLessThanEqual(UUID pointId, YearMonth periodEnd);
    List<RewardHistoryMetrics> getRewardHistoryMetricsByPointIdAndPeriodGreaterThanEqualAndPeriodLessThanEqual(UUID pointId, YearMonth periodStart, YearMonth periodEnd);
}
