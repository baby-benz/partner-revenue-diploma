package ru.itmo.common.web.dto.response.calcscheme;

import java.util.List;

public record CalcScheme(String id, List<CalcRule> calcRules, boolean isRecalc) {
}
