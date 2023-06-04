package ru.itmo.common.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.itmo.common.web.dto.request.PeriodFilter;

import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.Objects;

public class PeriodFilterValidator implements ConstraintValidator<ValidPeriodFilter, PeriodFilter> {

    @Override
    public void initialize(ValidPeriodFilter validPeriodFilter) {
    }

    @Override
    public boolean isValid(PeriodFilter filter, ConstraintValidatorContext cxt) {
        if (filter == null) {
            return true;
        }

        YearMonth periodStart = filter.start();
        YearMonth periodEnd = filter.end();

        if (periodStart == null && periodEnd == null) {
            return false;
        }

        YearMonth now = YearMonth.now(ZoneOffset.UTC);

        if (periodStart == null) {
            return !periodEnd.isAfter(now);
        }

        return !periodStart.isAfter(Objects.requireNonNullElse(periodEnd, now));
    }
}
