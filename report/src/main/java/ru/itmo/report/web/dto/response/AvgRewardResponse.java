package ru.itmo.report.web.dto.response;

import java.math.BigDecimal;
import java.time.YearMonth;

public record AvgRewardResponse(BigDecimal avg, YearMonth periodStart, YearMonth periodEnd) {
}
