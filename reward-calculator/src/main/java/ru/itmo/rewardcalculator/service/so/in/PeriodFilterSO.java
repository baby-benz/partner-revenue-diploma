package ru.itmo.rewardcalculator.service.so.in;

import ru.itmo.common.web.dto.request.PeriodFilter;

import java.time.YearMonth;

public record PeriodFilterSO(YearMonth periodStart, YearMonth periodEnd) {
    public static PeriodFilterSO fromPeriodFilter(PeriodFilter filter) {
        return filter == null ? null : new PeriodFilterSO(filter.start(), filter.end());
    }
    public static PeriodFilter toPeriodFilter(PeriodFilterSO filter) {
        return filter == null ? null : new PeriodFilter(filter.periodStart, filter.periodEnd);
    }
}
