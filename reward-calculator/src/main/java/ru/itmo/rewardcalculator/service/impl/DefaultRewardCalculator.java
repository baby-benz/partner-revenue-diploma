package ru.itmo.rewardcalculator.service.impl;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.common.web.dto.response.calcscheme.CalcRule;
import ru.itmo.rewardcalculator.service.RewardCalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class DefaultRewardCalculator implements RewardCalculator {
    private final Expression calculationExpression;
    private final String amountCodeWord;
    private final String interestRateCodeWord;
    private final String bonusCodeWord;

    @Override
    public RewardWithFormula calcReward(BigDecimal amount, BigDecimal eventAmountTotal,
                                        final List<CalcRule> calcRules) {
        BigDecimal rewardTotal = BigDecimal.ZERO;
        StringBuilder formulaBuilder = new StringBuilder();
        boolean isCrossed = eventAmountTotal == BigDecimal.ZERO;

        for (int i = 0; i < calcRules.size() - 1; i++) {
            CalcRule curRule = calcRules.get(i);
            BigDecimal curRuleAmount = BigDecimal.valueOf(curRule.amount());
            BigDecimal nextRuleAmount = BigDecimal.valueOf(calcRules.get(i + 1).amount());

            if (eventAmountTotal.compareTo(curRuleAmount) == 0) {
                if (eventAmountTotal.add(amount).compareTo(nextRuleAmount) > 0) {
                    AmountWithReward amountWithReward = processLeftAmount(eventAmountTotal, nextRuleAmount, curRule,
                            formulaBuilder, isCrossed);
                    isCrossed = true;
                    rewardTotal = rewardTotal.add(amountWithReward.reward);
                    amount = amount.subtract(amountWithReward.amount);
                    eventAmountTotal = nextRuleAmount;
                } else {
                    rewardTotal = rewardTotal.add(processBorderAmount(amount, curRule, formulaBuilder));
                    return new RewardWithFormula(rewardTotal, formulaBuilder.toString());
                }
            } else {
                int nextRuleComparison = eventAmountTotal.compareTo(nextRuleAmount);
                if (nextRuleComparison < 0) {
                    if (eventAmountTotal.add(amount).compareTo(nextRuleAmount) > 0) {
                        AmountWithReward amountWithReward = processLeftAmount(eventAmountTotal, nextRuleAmount, curRule,
                                formulaBuilder, isCrossed);
                        isCrossed = true;
                        rewardTotal = rewardTotal.add(amountWithReward.reward);
                        amount = amount.subtract(amountWithReward.amount);
                        eventAmountTotal = eventAmountTotal.add(amountWithReward.amount);
                    } else {
                        rewardTotal = rewardTotal.add(processRightAmount(amount, curRule, formulaBuilder, isCrossed));
                        return new RewardWithFormula(rewardTotal, formulaBuilder.toString());
                    }
                } else if (nextRuleComparison == 0) {
                    isCrossed = true;
                }
            }
        }

        rewardTotal = rewardTotal.add(
                processAmountForSingleRule(amount, calcRules.get(calcRules.size() - 1), formulaBuilder, isCrossed)
        );

        return new RewardWithFormula(rewardTotal, formulaBuilder.toString());
    }

    @Override
    public RewardWithFormula calcReward(BigDecimal amount, float interestRate) {
        return new RewardWithFormula(
                evaluateCalcExpression(amount, interestRate, 0),
                buildFormula(amount, interestRate, 0)
        );
    }

    @Override
    public RecalcRewardWithFormula calcRewardWithRecalc(BigDecimal amount, BigDecimal eventAmountTotal,
                                                        final List<CalcRule> calcRules) {
        StringBuilder formulaBuilder = new StringBuilder();
        Integer crossedRuleNumber = null;

        for (int i = 0; i < calcRules.size() - 1; i++) {
            CalcRule curRule = calcRules.get(i);
            BigDecimal curRuleAmount = BigDecimal.valueOf(curRule.amount());
            BigDecimal nextRuleAmount = BigDecimal.valueOf(calcRules.get(i + 1).amount());

            final boolean isCrossed = eventAmountTotal.add(amount).compareTo(nextRuleAmount) >= 0;

            if (eventAmountTotal.compareTo(curRuleAmount) == 0) {
                if (isCrossed) {
                    crossedRuleNumber = i + 1;
                } else {
                    return new RecalcRewardWithFormula(
                            Optional.ofNullable(crossedRuleNumber),
                            new RewardWithFormula(
                                    processBorderAmount(amount, curRule, formulaBuilder),
                                    formulaBuilder.toString()
                            )
                    );
                }
            } else if (eventAmountTotal.compareTo(nextRuleAmount) < 0) {
                if (isCrossed) {
                    crossedRuleNumber = i + 1;
                } else {
                    return new RecalcRewardWithFormula(
                            Optional.ofNullable(crossedRuleNumber),
                            new RewardWithFormula(
                                    processRightAmount(amount, curRule, formulaBuilder,
                                            crossedRuleNumber != null),
                                    formulaBuilder.toString()
                            )
                    );
                }
            }
        }

        if (eventAmountTotal.compareTo(BigDecimal.valueOf(calcRules.get(calcRules.size() - 1).amount())) == 0) {
            crossedRuleNumber = calcRules.size() - 1;
        }

        Optional<Integer> optionalCrossedRuleNumber = Optional.ofNullable(crossedRuleNumber);
        return new RecalcRewardWithFormula(
                optionalCrossedRuleNumber,
                new RewardWithFormula(
                        processAmountForSingleRule(amount, calcRules.get(calcRules.size() - 1), formulaBuilder,
                                optionalCrossedRuleNumber.isPresent()),
                        formulaBuilder.toString()
                )
        );
    }

    private BigDecimal processAmountForSingleRule(BigDecimal amount, CalcRule calcRule, StringBuilder formulaBuilder,
                                                  boolean isCrossed) {
        if (isCrossed) {
            return processBorderAmount(amount, calcRule, formulaBuilder);
        } else {
            return processRightAmount(amount, calcRule, formulaBuilder, false);
        }
    }

    private BigDecimal processBorderAmount(BigDecimal amount, CalcRule calcRule, StringBuilder formulaBuilder) {
        BigDecimal reward = evaluateCalcExpression(amount, calcRule.interestRate(), calcRule.bonus());
        appendToFormula(formulaBuilder, amount, calcRule.interestRate(), calcRule.bonus());
        return reward;
    }

    private BigDecimal processRightAmount(BigDecimal amount, CalcRule calcRule, StringBuilder formulaBuilder,
                                          boolean isCrossed) {
        BigDecimal reward = evaluateCalcExpression(amount, calcRule.interestRate(), 0);
        if (isCrossed) {
            appendToFormula(formulaBuilder, amount, calcRule.interestRate(), calcRule.bonus());
        } else {
            appendToFormula(formulaBuilder, amount, calcRule.interestRate(), 0);
        }
        return reward;
    }

    private AmountWithReward processLeftAmount(BigDecimal eventAmountTotal, BigDecimal nextRuleAmount, CalcRule curRule,
                                               StringBuilder formulaBuilder, boolean isCrossed) {
        BigDecimal calcRewardFor = nextRuleAmount.subtract(eventAmountTotal);
        BigDecimal reward;
        if (isCrossed) {
            appendToFormula(formulaBuilder, calcRewardFor, curRule.interestRate(), curRule.bonus());
            reward = evaluateCalcExpression(calcRewardFor, curRule.interestRate(), curRule.bonus());
        } else {
            appendToFormula(formulaBuilder, calcRewardFor, curRule.interestRate(), 0);
            reward = evaluateCalcExpression(calcRewardFor, curRule.interestRate(), 0);
        }
        return new AmountWithReward(calcRewardFor, reward);
    }

    private BigDecimal evaluateCalcExpression(BigDecimal amount, float interestRate, int bonus) {
        try {
            return calculationExpression
                    .with(amountCodeWord, amount)
                    .and(interestRateCodeWord, interestRate)
                    .and(bonusCodeWord, bonus).evaluate().getNumberValue();
        } catch (EvaluationException e) {
            log.error("Ошибка во время подстановки значений в формулу. Переданы неверные аргументы");
            log.debug(e.toString());
            throw new RuntimeException(e);
        } catch (ParseException e) {
            log.error("Ошибка во время парсинга формулы. Формула содержит ошибки");
            log.debug(e.toString());
            throw new RuntimeException(e);
        }
    }

    private void appendToFormula(StringBuilder formulaBuilder, BigDecimal amount, float interestRate, int bonus) {
        String formula = buildFormula(amount, interestRate, bonus);
        if (!formulaBuilder.isEmpty()) {
            formulaBuilder.append(" + ");
        }
        formulaBuilder.append(formula);
    }

    private String buildFormula(BigDecimal amount, float interestRate, int bonus) {
        return "(" + calculationExpression.getExpressionString()
                .replace("amount", amount.setScale(2, RoundingMode.HALF_EVEN).toString())
                .replace("interest_rate", String.valueOf(interestRate))
                .replace("bonus", String.valueOf(bonus)) + ")";
    }

    private record AmountWithReward(BigDecimal amount, BigDecimal reward) {
    }
}
