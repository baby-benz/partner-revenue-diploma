package ru.itmo.calcschemeservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itmo.calcschemeservice.domain.entity.CalcRule;
import ru.itmo.calcschemeservice.domain.entity.CalcScheme;
import ru.itmo.calcschemeservice.repository.CalcRuleRepository;
import ru.itmo.calcschemeservice.repository.CalcSchemeRepository;
import ru.itmo.calcschemeservice.service.CalcSchemeService;
import ru.itmo.calcschemeservice.service.so.in.CreateCalcSchemeSO;
import ru.itmo.calcschemeservice.service.so.out.CreatedCalcRuleSO;
import ru.itmo.calcschemeservice.service.so.out.CreatedCalcSchemeSO;
import ru.itmo.calcschemeservice.service.so.out.FullCalcRuleSO;
import ru.itmo.calcschemeservice.service.so.out.FullCalcSchemeSO;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.HttpErrorCause;
import ru.itmo.common.exception.cause.NotFoundErrorCause;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultCalcSchemeService implements CalcSchemeService {
    private final CalcSchemeRepository calcSchemeRepository;
    private final CalcRuleRepository calcRuleRepository;

    @Override
    public CreatedCalcSchemeSO createCalcScheme(CreateCalcSchemeSO schemeData) {
        List<CalcRule> savedRules = calcRuleRepository.saveAll(
                schemeData.calcRules().stream().map((rule) -> new CalcRule(
                        UUID.randomUUID(), rule.amount(), rule.interestRate(), rule.bonus()
                )).toList()
        );
        CalcScheme savedScheme = calcSchemeRepository.save(
                new CalcScheme(UUID.randomUUID(), schemeData.isRecalc(), savedRules)
        );
        return new CreatedCalcSchemeSO(
                savedScheme.getId().toString(),
                savedScheme.getCalcRules().stream().map((rule) ->
                        new CreatedCalcRuleSO(rule.getId().toString(), rule.getAmount(), rule.getInterestRate(), rule.getBonus())
                ).toList(),
                savedScheme.isRecalc()
        );
    }

    @Override
    public FullCalcSchemeSO getCalcScheme(String calcSchemeId) {
        UUID id = UUID.fromString(calcSchemeId);
        if (!calcSchemeRepository.existsById(id)) {
            log.debug("Calc scheme to get with id " + calcSchemeId + " was not found");
            throw new HttpStatusCodeException(
                    new NotFoundErrorCause(List.of(HttpErrorCause.NotFound.CALC_SCHEME_NOT_FOUND)),
                    calcSchemeId
            );
        }

        log.debug("Calc scheme to get with id " + calcSchemeId + " was successfully found");
        CalcScheme calcScheme = calcSchemeRepository.getReferenceById(id);

        return new FullCalcSchemeSO(
                calcSchemeId,
                calcScheme.getCalcRules().stream().map((rule) ->
                        new FullCalcRuleSO(rule.getId().toString(), rule.getAmount(), rule.getInterestRate(), rule.getBonus())
                ).toList(),
                calcScheme.isRecalc()
        );
    }

    @Override
    public boolean profileExists(String calcSchemeId) {
        return calcSchemeRepository.existsById(UUID.fromString(calcSchemeId));
    }
}
