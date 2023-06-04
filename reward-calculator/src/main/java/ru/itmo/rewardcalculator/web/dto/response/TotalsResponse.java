package ru.itmo.rewardcalculator.web.dto.response;

import java.math.BigDecimal;

public record TotalsResponse(BigDecimal eventAmountTotal, BigDecimal rewardAmountTotal) {
}
