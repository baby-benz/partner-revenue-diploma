package ru.itmo.calcscheme.web.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record CreateCalcRuleRequest(long amount, @DecimalMin("0.001") @DecimalMax("0.5") float interestRate, int bonus) {
}
