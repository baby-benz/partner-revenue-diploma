package ru.itmo.calcscheme.web.dto.response;

import java.util.List;

public record CreatedCalcSchemeResponse(String id, List<CreatedCalcRuleResponse> calcRules, boolean isRecalc) {
}
