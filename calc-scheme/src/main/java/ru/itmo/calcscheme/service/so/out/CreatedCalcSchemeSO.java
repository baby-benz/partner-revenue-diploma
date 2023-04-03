package ru.itmo.calcscheme.service.so.out;

import java.util.List;

public record CreatedCalcSchemeSO(String id, List<CreatedCalcRuleSO> calcRules, boolean isRecalc) {
}
