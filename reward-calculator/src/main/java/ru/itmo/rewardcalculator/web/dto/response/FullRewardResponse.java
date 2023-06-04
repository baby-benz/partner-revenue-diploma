package ru.itmo.rewardcalculator.web.dto.response;

import java.math.BigDecimal;

public record FullRewardResponse(String id, BigDecimal eventAmount, BigDecimal rewardAmount, String formula, String timestamp,
                                 String profileId, String pointId, String eventId, String calcSchemeId) {
}
