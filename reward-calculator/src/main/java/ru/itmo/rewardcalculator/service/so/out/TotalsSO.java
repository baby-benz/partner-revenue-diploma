package ru.itmo.rewardcalculator.service.so.out;

import java.math.BigDecimal;

public record TotalsSO(BigDecimal eventAmountTotal, BigDecimal rewardAmountTotal) {
}
