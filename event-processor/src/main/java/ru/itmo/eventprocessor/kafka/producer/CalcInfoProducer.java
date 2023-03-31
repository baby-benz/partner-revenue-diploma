package ru.itmo.eventprocessor.kafka.producer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.itmo.common.constant.KafkaTopics;

import ru.itmo.eventprocessor.domain.message.CalcInfo.CalcInfoMessage;

import java.util.concurrent.CountDownLatch;

@Component
@RequiredArgsConstructor
public class CalcInfoProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    @Getter
    private final CountDownLatch latch = new CountDownLatch(1);
    @Getter
    private Long messagePublished = 0L;

    public void sendMessage(CalcInfoMessage message) {
        kafkaTemplate.send(KafkaTopics.CALC_INFO, message.toByteArray());
        messagePublished++;
        latch.countDown();
    }
}
