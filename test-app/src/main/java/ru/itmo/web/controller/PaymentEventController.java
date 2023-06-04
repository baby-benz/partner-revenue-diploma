package ru.itmo.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.common.domain.message.PaymentEvent;
import ru.itmo.common.service.util.BigDecimalUtil;
import ru.itmo.common.service.util.TimestampUtil;
import ru.itmo.kafka.PaymentEventProducer;
import ru.itmo.web.dto.PaymentEventRequest;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@RequiredArgsConstructor
@RestController
public class PaymentEventController {
    private final PaymentEventProducer paymentEventProducer;

    @PostMapping("test/payment")
    public void publishCalcInfoMessage(@RequestBody PaymentEventRequest paymentEvent) {
        paymentEventProducer.sendMessage(PaymentEvent.PaymentEventMessage.newBuilder()
                .setId(paymentEvent.eventId())
                .setAmount(BigDecimalUtil.toProtoDecimalValue(paymentEvent.amount()))
                .setTimestamp(OffsetDateTime.now(ZoneId.systemDefault()).toString())
                .setPointId(paymentEvent.pointId())
                .setProfileId(paymentEvent.profileId())
                .build());
    }
}
