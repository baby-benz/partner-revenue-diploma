package ru.itmo.rewardcalculator.service.so.out;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record FullRewardSO(String eventId, BigDecimal eventAmount, BigDecimal rewardAmount, OffsetDateTime timestamp,
                           String formula, String profileId, String pointId, String calcSchemeId) {
}
