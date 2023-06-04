package ru.itmo.calcschemeservice.web.dto.response;

import java.util.List;

public record FullCalcSchemeResponse(String id, List<FullCalcRuleResponse> calcRules, boolean isRecalc) {
}
