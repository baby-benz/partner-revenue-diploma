package ru.itmo.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.itmo.common.constant.KafkaTopics;
import ru.itmo.common.domain.message.PaymentEvent;

@RequiredArgsConstructor
@Component
public class PaymentEventProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void sendMessage(PaymentEvent.PaymentEventMessage message) {
        kafkaTemplate.send(KafkaTopics.PAYMENT_EVENT, message.toByteArray());
    }
}
