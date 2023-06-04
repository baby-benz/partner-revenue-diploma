package ru.itmo.rewardcalculator.service.so.out;

import java.math.BigDecimal;
import java.time.YearMonth;

public record HistoryTotalsSO(BigDecimal eventAmountTotal, BigDecimal rewardAmountTotal, YearMonth period) {
}
