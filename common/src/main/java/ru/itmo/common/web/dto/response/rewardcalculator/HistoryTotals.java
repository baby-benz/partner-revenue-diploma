package ru.itmo.common.web.dto.response.rewardcalculator;

import java.math.BigDecimal;
import java.time.YearMonth;

public record HistoryTotals(BigDecimal eventAmountTotal, BigDecimal rewardAmountTotal, YearMonth period) {
}
