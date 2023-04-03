package ru.itmo.calcscheme.web.dto.request;

import jakarta.validation.Valid;
import ru.itmo.calcscheme.web.validation.ValidCalcRules;

import java.util.List;

public record CreateCalcSchemeRequest(@ValidCalcRules List<@Valid CreateCalcRuleRequest> calcRules, boolean isRecalc) {
}
