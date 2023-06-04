package ru.itmo.rewardcalculator.kafka.producer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.itmo.common.constant.KafkaTopics;
import ru.itmo.common.domain.message.CalculatedReward;

import java.util.concurrent.CountDownLatch;

@Component
@RequiredArgsConstructor
public class CalculatedRewardProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    @Getter
    @Setter
    private CountDownLatch latch = new CountDownLatch(1);
    @Getter
    private Long messagePublished = 0L;

    public void sendMessage(CalculatedReward.RewardMessage message) {
        kafkaTemplate.send(KafkaTopics.CALCULATED_REWARD, message.toByteArray());
        messagePublished++;
        latch.countDown();
    }
}
