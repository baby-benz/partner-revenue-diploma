package ru.itmo.rewardcalculator.service;

import ru.itmo.common.web.dto.response.calcscheme.CalcRule;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RewardCalculator {
    RewardWithFormula calcReward(BigDecimal amount, BigDecimal eventAmountTotal, final List<CalcRule> calcRules);
    RewardWithFormula calcReward(BigDecimal amount, float interestRate);
    RecalcRewardWithFormula calcRewardWithRecalc(BigDecimal amount, BigDecimal eventAmountTotal, final List<CalcRule> calcRules);

    record RewardWithFormula(BigDecimal reward, String formula) {
    }

    record RecalcRewardWithFormula(Optional<Integer> crossedRuleNumber, RewardWithFormula rewardWithFormula) {
    }
}
