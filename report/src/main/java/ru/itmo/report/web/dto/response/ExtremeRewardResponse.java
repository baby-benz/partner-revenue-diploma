package ru.itmo.report.web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.itmo.rewardcalculator.service.so.in.PeriodFilterSO;

public record ExtremeRewardResponse(MaxRewardResponse max, MinRewardResponse min, @JsonInclude(JsonInclude.Include.NON_NULL) PeriodFilterSO periodFilter) {
}
