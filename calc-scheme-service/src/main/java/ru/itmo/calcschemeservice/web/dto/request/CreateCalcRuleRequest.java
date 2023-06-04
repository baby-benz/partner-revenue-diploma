package ru.itmo.calcschemeservice.web.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record CreateCalcRuleRequest(long amount, @DecimalMin("0.001") @DecimalMax("0.5") float interestRate, @Min(0) int bonus) {
}
