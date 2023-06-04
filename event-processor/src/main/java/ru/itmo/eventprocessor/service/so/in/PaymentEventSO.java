package ru.itmo.eventprocessor.service.so.in;

import ru.itmo.common.domain.message.PaymentEvent;
import ru.itmo.common.service.util.TimestampUtil;
import ru.itmo.common.service.util.BigDecimalUtil;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentEventSO(String id, BigDecimal amount, OffsetDateTime timestamp, String profileId, String pointId) {
    public static PaymentEventSO fromPaymentEventMessage(PaymentEvent.PaymentEventMessage paymentEventMessage) {
        return new PaymentEventSO(paymentEventMessage.getId(),
                BigDecimalUtil.toJavaBigDecimal(paymentEventMessage.getAmount()),
                TimestampUtil.fromString(paymentEventMessage.getTimestamp()),
                paymentEventMessage.getProfileId(),
                paymentEventMessage.getPointId());
    }
}
