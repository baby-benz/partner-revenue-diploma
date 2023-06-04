package ru.itmo.rewardcalculator.web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.itmo.common.web.dto.request.PeriodFilter;

public record ExtremeRewardResponse(MaxRewardResponse max, MinRewardResponse min, @JsonInclude(JsonInclude.Include.NON_NULL) PeriodFilter periodFilter) {
}
