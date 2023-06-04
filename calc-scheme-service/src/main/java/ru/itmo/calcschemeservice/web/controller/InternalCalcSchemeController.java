package ru.itmo.calcschemeservice.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.calcschemeservice.service.CalcSchemeService;
import ru.itmo.calcschemeservice.service.so.out.FullCalcSchemeSO;
import ru.itmo.calcschemeservice.web.dto.response.FullCalcRuleResponse;
import ru.itmo.calcschemeservice.web.dto.response.FullCalcSchemeResponse;
import ru.itmo.common.constant.InternalEndpoint;
import ru.itmo.common.web.validation.ValidUUID;

@Validated
@RequiredArgsConstructor
@RestController
public class InternalCalcSchemeController {
    private final CalcSchemeService calcSchemeService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(InternalEndpoint.CalcScheme.HEAD_CHECK_CALC_SCHEME)
    public void checkCalcScheme(@ValidUUID @PathVariable String calcSchemeId) {
        if (!calcSchemeService.profileExists(calcSchemeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(InternalEndpoint.CalcScheme.GET_CALC_SCHEME)
    public FullCalcSchemeResponse getCalcScheme(@ValidUUID @PathVariable String calcSchemeId) {
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
