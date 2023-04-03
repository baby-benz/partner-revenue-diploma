package ru.itmo.calcscheme.web.dto.response;

public record CreatedCalcRuleResponse(String id, long amount, float interestRate, int bonus) {
}
