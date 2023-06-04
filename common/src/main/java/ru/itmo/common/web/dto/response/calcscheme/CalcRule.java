package ru.itmo.common.web.dto.response.calcscheme;

public record CalcRule(String id, long amount, float interestRate, int bonus) {
}
