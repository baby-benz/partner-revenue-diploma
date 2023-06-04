package ru.itmo.rewardcalculator.domain.entity;

import io.hypersistence.utils.hibernate.type.basic.YearMonthIntegerType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

@Getter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class RewardHistoryMetrics {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID pointId;

    @NotNull
    @OneToOne
    @JoinColumn(name = "reward_id", referencedColumnName = "event_id", columnDefinition = "UUID")
    private Reward periodLastReward;

    @NotNull
    @Column(scale = 6, precision = 18 )
    private BigDecimal rewardAmountTotal;

    @NotNull
    @Type(YearMonthIntegerType.class)
    @Column(columnDefinition = "integer")
    private YearMonth period;
}
