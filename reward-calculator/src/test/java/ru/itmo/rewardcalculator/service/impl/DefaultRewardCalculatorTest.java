package ru.itmo.rewardcalculator.service.impl;

import com.ezylang.evalex.Expression;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.itmo.common.web.dto.response.calcscheme.CalcRule;
import ru.itmo.rewardcalculator.service.RewardCalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultRewardCalculatorTest {
    final static Expression EXPRESSION = new Expression("amount * interest_rate + bonus");
    final static RewardCalculator REWARD_CALCULATOR = new DefaultRewardCalculator(
            EXPRESSION, "amount", "interest_rate", "bonus");

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @ParameterizedTest
    @MethodSource("provideRewardTestData")
    void when_calcReward_then_ok(List<CalcRule> rules, BigDecimal amount, BigDecimal eventAmountTotal,
                                 BigDecimal expectedReward, String expectedFormula,
                                 Optional<Integer> expectedCrossedRuleNumber, BigDecimal expectedRecalcReward,
                                 String expectedRecalcFormula) {
        final RewardCalculator.RewardWithFormula result = REWARD_CALCULATOR.calcReward(amount, eventAmountTotal, rules);

        final BigDecimal actualReward = result.reward().setScale(2, RoundingMode.HALF_EVEN);
        final String actualFormula = result.formula();

        assertEquals(expectedReward, actualReward);
        assertEquals(expectedFormula, actualFormula);

        final RewardCalculator.RecalcRewardWithFormula recalcResult = REWARD_CALCULATOR.calcRewardWithRecalc(
                amount, eventAmountTotal, rules
        );

        final Optional<Integer> actualCrossedRuleNumber = recalcResult.crossedRuleNumber();
        final BigDecimal actualRecalcReward = recalcResult.rewardWithFormula().reward()
                .setScale(2, RoundingMode.HALF_EVEN);
        final String actualRecalcFormula = recalcResult.rewardWithFormula().formula();

        assertEquals(expectedCrossedRuleNumber, actualCrossedRuleNumber);
        assertEquals(expectedRecalcReward, actualRecalcReward);
        assertEquals(expectedRecalcFormula, actualRecalcFormula);
    }

    private static Stream<Arguments> provideRewardTestData() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                new CalcRule("1", 0L, 0.05F, 0),
                                new CalcRule("2", 500L, 0.1F, 200),
                                new CalcRule("3", 1000L, 0.15F, 300)
                        ), BigDecimal.valueOf(2000L), BigDecimal.ZERO,
                        BigDecimal.valueOf(725L).setScale(2, RoundingMode.HALF_EVEN), "(500.00 * 0.05 + 0) + (500.00 * 0.1 + 200) + (1000.00 * 0.15 + 300)",
                        Optional.of(2), BigDecimal.valueOf(600L).setScale(2, RoundingMode.HALF_EVEN), "(2000.00 * 0.15 + 300)"
                ),
                Arguments.of(
                        List.of(
                                new CalcRule("1", 0L, 0.05F, 10),
                                new CalcRule("2", 500L, 0.1F, 200),
                                new CalcRule("3", 1000L, 0.15F, 300)
                        ), BigDecimal.valueOf(250L), BigDecimal.ZERO,
                        BigDecimal.valueOf(22.5).setScale(2, RoundingMode.HALF_EVEN), "(250.00 * 0.05 + 10)",
                        Optional.empty(), BigDecimal.valueOf(22.5).setScale(2, RoundingMode.HALF_EVEN), "(250.00 * 0.05 + 10)"
                ),
                Arguments.of(
                        List.of(
                                new CalcRule("1", 0L, 0.05F, 0),
                                new CalcRule("2", 500L, 0.1F, 200),
                                new CalcRule("3", 1000L, 0.15F, 300)
                        ), BigDecimal.valueOf(250L), BigDecimal.valueOf(550L),
                        BigDecimal.valueOf(25).setScale(2, RoundingMode.HALF_EVEN), "(250.00 * 0.1 + 0)",
                        Optional.empty(), BigDecimal.valueOf(25).setScale(2, RoundingMode.HALF_EVEN), "(250.00 * 0.1 + 0)"
                ),
                Arguments.of(
                        List.of(
                                new CalcRule("1", 0L, 0.05F, 0),
                                new CalcRule("2", 500L, 0.1F, 200),
                                new CalcRule("3", 1000L, 0.15F, 300)
                        ), BigDecimal.valueOf(250L), BigDecimal.valueOf(500L),
                        BigDecimal.valueOf(225).setScale(2, RoundingMode.HALF_EVEN), "(250.00 * 0.1 + 200)",
                        Optional.empty(), BigDecimal.valueOf(225).setScale(2, RoundingMode.HALF_EVEN), "(250.00 * 0.1 + 200)"
                ),
                Arguments.of(
                        List.of(
                                new CalcRule("1", 0L, 0.05F, 0),
                                new CalcRule("2", 500L, 0.1F, 200),
                                new CalcRule("3", 1000L, 0.15F, 300)
                        ), BigDecimal.valueOf(250L), BigDecimal.valueOf(1000L),
                        BigDecimal.valueOf(337.5).setScale(2, RoundingMode.HALF_EVEN), "(250.00 * 0.15 + 300)",
                        Optional.of(2), BigDecimal.valueOf(337.5).setScale(2, RoundingMode.HALF_EVEN), "(250.00 * 0.15 + 300)"
                ),
                Arguments.of(
                        List.of(
                                new CalcRule("1", 0L, 0.05F, 0),
                                new CalcRule("2", 500L, 0.1F, 200),
                                new CalcRule("3", 1000L, 0.15F, 300)
                        ), BigDecimal.valueOf(2000L), BigDecimal.valueOf(250L),
                        BigDecimal.valueOf(750L).setScale(2, RoundingMode.HALF_EVEN), "(250.00 * 0.05 + 0) + (500.00 * 0.1 + 200) + (1250.00 * 0.15 + 300)",
                        Optional.of(2), BigDecimal.valueOf(600L).setScale(2, RoundingMode.HALF_EVEN), "(2000.00 * 0.15 + 300)"
                ),
                Arguments.of(
                        List.of(
                                new CalcRule("1", 0L, 0.05F, 0),
                                new CalcRule("2", 500L, 0.1F, 200),
                                new CalcRule("3", 1000L, 0.15F, 300)
                        ), BigDecimal.valueOf(2000L), BigDecimal.valueOf(1250L),
                        BigDecimal.valueOf(300L).setScale(2, RoundingMode.HALF_EVEN), "(2000.00 * 0.15 + 0)",
                        Optional.empty(), BigDecimal.valueOf(300L).setScale(2, RoundingMode.HALF_EVEN), "(2000.00 * 0.15 + 0)"
                ),
                Arguments.of(
                        List.of(
                                new CalcRule("1", 0L, 0.05F, 0)
                        ), BigDecimal.valueOf(2000L), BigDecimal.ZERO,
                        BigDecimal.valueOf(100L).setScale(2, RoundingMode.HALF_EVEN), "(2000.00 * 0.05 + 0)",
                        Optional.of(0), BigDecimal.valueOf(100L).setScale(2, RoundingMode.HALF_EVEN), "(2000.00 * 0.05 + 0)"
                ),
                Arguments.of(
                        List.of(
                                new CalcRule("1", 0L, 0.05F, 0)
                        ), BigDecimal.valueOf(2000L), BigDecimal.valueOf(1300L),
                        BigDecimal.valueOf(100L).setScale(2, RoundingMode.HALF_EVEN), "(2000.00 * 0.05 + 0)",
                        Optional.empty(), BigDecimal.valueOf(100L).setScale(2, RoundingMode.HALF_EVEN), "(2000.00 * 0.05 + 0)"
                ),
                Arguments.of(
                        List.of(
                                new CalcRule("1", 0L, 0.05F, 2000),
                                new CalcRule("2", 500L, 0.1F, 200),
                                new CalcRule("3", 1000L, 0.15F, 300)
                        ), BigDecimal.valueOf(2000L), BigDecimal.ZERO,
                        BigDecimal.valueOf(2725L).setScale(2, RoundingMode.HALF_EVEN), "(500.00 * 0.05 + 2000) + (500.00 * 0.1 + 200) + (1000.00 * 0.15 + 300)",
                        Optional.of(2), BigDecimal.valueOf(600L).setScale(2, RoundingMode.HALF_EVEN), "(2000.00 * 0.15 + 300)"
                ),
                Arguments.of(
                        List.of(
                                new CalcRule("1", 0L, 0.05F, 2000)
                        ), BigDecimal.valueOf(2000L), BigDecimal.ZERO,
                        BigDecimal.valueOf(2100L).setScale(2, RoundingMode.HALF_EVEN), "(2000.00 * 0.05 + 2000)",
                        Optional.of(0), BigDecimal.valueOf(2100L).setScale(2, RoundingMode.HALF_EVEN), "(2000.00 * 0.05 + 2000)"
                )
        );
    }
}
