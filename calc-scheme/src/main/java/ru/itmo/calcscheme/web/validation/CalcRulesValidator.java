package ru.itmo.calcscheme.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.itmo.calcscheme.web.dto.request.CreateCalcRuleRequest;

import java.util.List;

public class CalcRulesValidator implements ConstraintValidator<ValidCalcRules, List<CreateCalcRuleRequest>> {
    @Override
    public void initialize(ValidCalcRules contactNumber) {
    }

    @Override
    public boolean isValid(List<CreateCalcRuleRequest> rulesField, ConstraintValidatorContext cxt) {
        CreateCalcRuleRequest previousCalcRule = null;
        for (CreateCalcRuleRequest rule : rulesField) {
            if (previousCalcRule == null) {
                previousCalcRule = rule;
            } else {
                if (previousCalcRule.amount() > rule.amount()
                        || (previousCalcRule.interestRate() == rule.interestRate()
                        && previousCalcRule.bonus() == rule.bonus())) {
                    return false;
                }
            }
        }
        return true;
    }

}
