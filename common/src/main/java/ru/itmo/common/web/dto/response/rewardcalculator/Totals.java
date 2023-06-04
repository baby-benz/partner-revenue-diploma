package ru.itmo.common.web.dto.response.rewardcalculator;

import java.math.BigDecimal;

public record Totals(BigDecimal eventAmountTotal, BigDecimal rewardAmountTotal) {
}
