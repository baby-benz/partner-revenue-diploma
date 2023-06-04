package ru.itmo.calcschemeservice.web.dto.response;

public record FullCalcRuleResponse(String id, long amount, float interestRate, long bonus) {
}
