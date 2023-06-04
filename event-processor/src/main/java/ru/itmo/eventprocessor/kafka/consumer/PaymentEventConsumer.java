package ru.itmo.eventprocessor.kafka.consumer;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.itmo.common.constant.KafkaTopics;
import ru.itmo.common.domain.message.PaymentEvent;
import ru.itmo.eventprocessor.service.EventService;
import ru.itmo.eventprocessor.service.so.in.PaymentEventSO;

import java.util.concurrent.CountDownLatch;

@RequiredArgsConstructor
@Component
public class PaymentEventConsumer {
    @Getter
    private final CountDownLatch latch = new CountDownLatch(1);
    @Getter
    private PaymentEvent.PaymentEventMessage eventPayload;
    private final EventService eventService;

    @KafkaListener(topics = KafkaTopics.PAYMENT_EVENT)
    public void consume(byte[] message) throws InvalidProtocolBufferException {
        eventPayload = PaymentEvent.PaymentEventMessage.parseFrom(message);
        eventService.process(PaymentEventSO.fromPaymentEventMessage(eventPayload));
        latch.countDown();
    }
}
