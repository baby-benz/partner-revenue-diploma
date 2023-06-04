package ru.itmo.web.dto;

import java.math.BigDecimal;

public record PaymentEventRequest(String eventId, BigDecimal amount, String profileId, String pointId) {
}
