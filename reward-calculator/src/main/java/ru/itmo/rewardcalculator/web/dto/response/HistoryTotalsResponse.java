package ru.itmo.rewardcalculator.web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;


public record HistoryTotalsResponse(List<TotalsWithDateResponse> totalsWithDateResponses,
                                    @JsonInclude(JsonInclude.Include.NON_NULL) YearMonth periodStart,
                                    @JsonInclude(JsonInclude.Include.NON_NULL) YearMonth periodEnd) {
    public record TotalsWithDateResponse(BigDecimal eventAmountTotal, BigDecimal rewardAmountTotal, YearMonth period) {
    }
}
