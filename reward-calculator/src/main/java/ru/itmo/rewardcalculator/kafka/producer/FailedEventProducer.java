package ru.itmo.rewardcalculator.kafka.producer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.itmo.common.constant.KafkaTopics;
import ru.itmo.common.domain.message.FailedCalcInfo;

import java.util.concurrent.CountDownLatch;

@Component
@RequiredArgsConstructor
public class FailedEventProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    @Getter
    private final CountDownLatch latch = new CountDownLatch(1);
    @Getter
    private Long messagePublished = 0L;

    public void sendMessage(FailedCalcInfo.FailedCalcInfoMessage message) {
        kafkaTemplate.send(KafkaTopics.FAILED_EVENT, message.toByteArray());
        messagePublished++;
        latch.countDown();
    }
}
