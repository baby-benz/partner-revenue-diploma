package ru.itmo.calcschemeservice.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.itmo.calcschemeservice.web.dto.request.CreateCalcRuleRequest;

import java.util.List;

public class CalcRulesValidator implements ConstraintValidator<ValidCalcRules, List<CreateCalcRuleRequest>> {
    @Override
    public void initialize(ValidCalcRules annotationInfo) {
    }

    @Override
    public boolean isValid(List<CreateCalcRuleRequest> rulesField, ConstraintValidatorContext cxt) {
        if (rulesField.get(0).amount() != 0L) {
            return false;
        }
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
