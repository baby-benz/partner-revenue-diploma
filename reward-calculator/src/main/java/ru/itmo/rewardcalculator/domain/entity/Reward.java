package ru.itmo.rewardcalculator.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Reward {
    @Id
    @Column(columnDefinition = "uuid", name = "event_id")
    private UUID eventId;

    @NotNull
    @Column(scale = 6, precision = 14)
    private BigDecimal eventAmount;

    @NotNull
    @Column(scale = 6, precision = 14)
    private BigDecimal rewardAmount;

    @NotNull
    @Column(scale = 6, precision = 18 )
    private BigDecimal eventAmountTotal;

    @NotNull
    @Column
    private String formula;

    @NotNull
    @Column
    private OffsetDateTime rewardTime;

    @NotNull
    @Column(columnDefinition = "uuid")
    private UUID profileId;

    @NotNull
    @Column(columnDefinition = "uuid")
    private UUID pointId;

    @NotNull
    @Column(columnDefinition = "uuid")
    private UUID calcSchemeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reward reward)) return false;
        return reward.getRewardAmount().compareTo(getRewardAmount()) == 0
                && Objects.equals(getEventId(), reward.getEventId())
                && Objects.equals(getRewardTime(), reward.getRewardTime())
                && Objects.equals(getFormula(), reward.getFormula())
                && Objects.equals(getEventAmountTotal(), reward.getEventAmountTotal())
                && Objects.equals(getPointId(), reward.getPointId())
                && Objects.equals(getCalcSchemeId(), reward.getCalcSchemeId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEventId(), getRewardAmount(), getRewardTime(), getFormula(), getEventAmountTotal(), getCalcSchemeId());
    }
}
