package ru.itmo.calcscheme.service.so.in;

import java.util.List;

public record CreateCalcSchemeSO(List<CreateCalcRuleSO> calcRules, boolean isRecalc) {
}
