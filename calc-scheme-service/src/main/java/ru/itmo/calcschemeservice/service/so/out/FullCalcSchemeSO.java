package ru.itmo.calcschemeservice.service.so.out;

import java.util.List;

public record FullCalcSchemeSO(String id, List<FullCalcRuleSO> calcRules, boolean isRecalc) {
}
