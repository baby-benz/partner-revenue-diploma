package ru.itmo.report.web.dto.response;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MaxRewardResponse(String pointId, BigDecimal amount, YearMonth period) {
}
