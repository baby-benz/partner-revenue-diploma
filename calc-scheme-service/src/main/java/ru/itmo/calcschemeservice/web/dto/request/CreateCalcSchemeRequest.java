package ru.itmo.calcschemeservice.web.dto.request;

import jakarta.validation.Valid;
import ru.itmo.calcschemeservice.web.validation.ValidCalcRules;

import java.util.List;

public record CreateCalcSchemeRequest(@ValidCalcRules List<@Valid CreateCalcRuleRequest> calcRules, boolean isRecalc) {
}
