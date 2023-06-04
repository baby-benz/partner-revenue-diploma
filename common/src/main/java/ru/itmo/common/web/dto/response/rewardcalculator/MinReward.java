package ru.itmo.common.web.dto.response.rewardcalculator;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MinReward(String pointId, BigDecimal amount, YearMonth period) {
}
