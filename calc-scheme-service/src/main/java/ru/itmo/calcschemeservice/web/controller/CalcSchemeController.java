package ru.itmo.calcschemeservice.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itmo.calcschemeservice.service.CalcSchemeService;
import ru.itmo.calcschemeservice.service.so.in.CreateCalcRuleSO;
import ru.itmo.calcschemeservice.service.so.in.CreateCalcSchemeSO;
import ru.itmo.calcschemeservice.service.so.out.CreatedCalcSchemeSO;
import ru.itmo.calcschemeservice.service.so.out.FullCalcSchemeSO;
import ru.itmo.calcschemeservice.web.dto.request.CreateCalcSchemeRequest;
import ru.itmo.calcschemeservice.web.dto.response.CreatedCalcRuleResponse;
import ru.itmo.calcschemeservice.web.dto.response.CreatedCalcSchemeResponse;
import ru.itmo.calcschemeservice.web.dto.response.FullCalcRuleResponse;
import ru.itmo.calcschemeservice.web.dto.response.FullCalcSchemeResponse;
import ru.itmo.common.constant.Endpoint;

@Validated
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

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(Endpoint.CalcScheme.GET_FULL)
    public FullCalcSchemeResponse getCalcScheme(@NotNull @PathVariable String calcSchemeId) {
        FullCalcSchemeSO calcScheme = calcSchemeService.getCalcScheme(calcSchemeId);

        return new FullCalcSchemeResponse(
                calcScheme.id(),
                calcScheme.calcRules().stream().map(
                        (rule) -> new FullCalcRuleResponse(
                                rule.id(), rule.amount(), rule.interestRate(), rule.bonus()
                        )
                ).toList(),
                calcScheme.isRecalc());
    }
}
