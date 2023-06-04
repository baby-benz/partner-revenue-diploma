package ru.itmo.rewardcalculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itmo.rewardcalculator.domain.entity.Reward;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface RewardRepository extends JpaRepository<Reward, UUID>, JpaSpecificationExecutor<Reward> {
    List<Reward> getRewardsByPointId(UUID pointId);

    Reward findTopByOrderByRewardTimeDesc();
    @Query("SELECT r FROM Reward AS r WHERE r.rewardTime >= :time AND r.eventAmountTotal = (SELECT MAX(rr.eventAmountTotal) FROM Reward AS rr WHERE rr.pointId = r.pointId)")
    List<Reward> findRewardWithMaxEventTotalAndTimestamp(@Param("time") OffsetDateTime time);
    @Query("SELECT SUM(r.rewardAmount) FROM Reward AS r WHERE r.pointId = :pointId AND r.rewardTime >= :time")
    BigDecimal sumByRewardAndTimestamp(@Param("pointId") UUID pointId, @Param("time") OffsetDateTime time);
    Reward findFirstByPointIdAndRewardTimeGreaterThanEqualOrderByRewardAmountDesc(UUID pointId, OffsetDateTime start);
    Reward findFirstByPointIdAndRewardTimeLessThanEqualOrderByRewardAmountDesc(UUID pointId, OffsetDateTime end);
    Reward findFirstByPointIdAndRewardTimeGreaterThanEqualAndRewardTimeLessThanEqualOrderByRewardAmountDesc(UUID pointId, OffsetDateTime start, OffsetDateTime end);
    Reward findFirstByPointIdAndRewardTimeGreaterThanEqualOrderByRewardAmountAsc(UUID pointId, OffsetDateTime start);
    Reward findFirstByPointIdAndRewardTimeLessThanEqualOrderByRewardAmountAsc(UUID pointId, OffsetDateTime end);
    Reward findFirstByPointIdAndRewardTimeGreaterThanEqualAndRewardTimeLessThanEqualOrderByRewardAmountAsc(UUID pointId, OffsetDateTime start, OffsetDateTime end);
}
