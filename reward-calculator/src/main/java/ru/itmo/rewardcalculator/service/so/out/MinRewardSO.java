package ru.itmo.rewardcalculator.service.so.out;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MinRewardSO(String pointId, BigDecimal amount, YearMonth period) {
}
