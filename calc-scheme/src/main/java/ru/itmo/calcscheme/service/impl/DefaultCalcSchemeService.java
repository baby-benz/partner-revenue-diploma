package ru.itmo.calcscheme.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.calcscheme.domain.entity.CalcRule;
import ru.itmo.calcscheme.domain.entity.CalcScheme;
import ru.itmo.calcscheme.repository.CalcRuleRepository;
import ru.itmo.calcscheme.repository.CalcSchemeRepository;
import ru.itmo.calcscheme.service.CalcSchemeService;
import ru.itmo.calcscheme.service.so.in.CreateCalcSchemeSO;
import ru.itmo.calcscheme.service.so.out.CreatedCalcRuleSO;
import ru.itmo.calcscheme.service.so.out.CreatedCalcSchemeSO;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DefaultCalcSchemeService implements CalcSchemeService {
    private final CalcSchemeRepository calcSchemeRepository;
    private final CalcRuleRepository calcRuleRepository;

    @Override
    public CreatedCalcSchemeSO createCalcScheme(CreateCalcSchemeSO schemeData) {
        List<CalcRule> savedRules = calcRuleRepository.saveAll(
                schemeData.calcRules().stream().map((rule) -> new CalcRule(
                        UUID.randomUUID().toString(), rule.amount(), rule.interestRate(), rule.bonus()
                )).toList()
        );
        CalcScheme savedScheme = calcSchemeRepository.save(
                new CalcScheme(UUID.randomUUID().toString(), schemeData.isRecalc(), savedRules)
        );
        return new CreatedCalcSchemeSO(
                savedScheme.getId(),
                savedScheme.getCalcRules().stream().map((rule) ->
                        new CreatedCalcRuleSO(rule.getId(), rule.getAmount(), rule.getInterestRate(), rule.getBonus())
                ).toList(),
                savedScheme.isRecalc()
        );
    }

    @Override
    public boolean profileExists(String calcSchemeId) {
        return calcSchemeRepository.existsById(calcSchemeId);
    }
}
