package ru.itmo.calcschemeservice.web.dto.response;

import java.util.List;

public record CreatedCalcSchemeResponse(String id, List<CreatedCalcRuleResponse> calcRules, boolean isRecalc) {
}
