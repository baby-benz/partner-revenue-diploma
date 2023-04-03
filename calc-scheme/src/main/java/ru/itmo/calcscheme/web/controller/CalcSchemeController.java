package ru.itmo.calcscheme.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.itmo.calcscheme.service.CalcSchemeService;
import ru.itmo.calcscheme.service.so.in.CreateCalcRuleSO;
import ru.itmo.calcscheme.service.so.in.CreateCalcSchemeSO;
import ru.itmo.calcscheme.service.so.out.CreatedCalcSchemeSO;
import ru.itmo.calcscheme.web.dto.request.CreateCalcSchemeRequest;
import ru.itmo.calcscheme.web.dto.response.CreatedCalcRuleResponse;
import ru.itmo.calcscheme.web.dto.response.CreatedCalcSchemeResponse;
import ru.itmo.common.constant.Endpoint;

@RequiredArgsConstructor
@RestController
public class CalcSchemeController {
    private final CalcSchemeService calcSchemeService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(Endpoint.CalcScheme.POST_NEW)
    public CreatedCalcSchemeResponse createCalcScheme(@Valid @RequestBody CreateCalcSchemeRequest request) {
        CreatedCalcSchemeSO createdCalcScheme = calcSchemeService.createCalcScheme(
                new CreateCalcSchemeSO(
                        request.calcRules().stream().map((rule) ->
                                new CreateCalcRuleSO(rule.amount(), rule.interestRate(), rule.bonus())).toList(),
                        request.isRecalc()
                )
        );
        return new CreatedCalcSchemeResponse(
                createdCalcScheme.id(),
                createdCalcScheme.calcRules().stream().map(
                        (rule) -> new CreatedCalcRuleResponse(
                                rule.id(), rule.amount(), rule.interestRate(), rule.bonus()
                        )
                ).toList(),
                createdCalcScheme.isRecalc());
    }
}
