package ru.itmo.rewardcalculator.service.so.in;

import java.math.BigDecimal;

public record CalcInfoSO(String eventId, BigDecimal amount, String profileId, String pointId) {
}
