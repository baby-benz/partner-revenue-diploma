package ru.itmo.calcschemeservice.web.dto.response;

public record CreatedCalcRuleResponse(String id, long amount, float interestRate, long bonus) {
}
