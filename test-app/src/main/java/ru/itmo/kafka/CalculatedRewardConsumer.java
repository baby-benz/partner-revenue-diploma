package ru.itmo.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.itmo.common.constant.KafkaTopics;
import ru.itmo.common.domain.message.CalculatedReward;
import ru.itmo.common.service.util.BigDecimalUtil;

@Slf4j
@Component
public class CalculatedRewardConsumer {
    @KafkaListener(topics = KafkaTopics.CALCULATED_REWARD)
    public void consume(byte[] message) throws InvalidProtocolBufferException {
        CalculatedReward.RewardMessage reward = CalculatedReward.RewardMessage.parseFrom(message);
        log.info("[PARSED REWARD] │ EventID: {} ¦ PointID: {} ¦ Amount: {} ¦ Reward: {}",
                reward.getEventId(),
                reward.getPointId(),
                BigDecimalUtil.toJavaBigDecimal(reward.getEventAmount()),
                BigDecimalUtil.toJavaBigDecimal(reward.getRewardAmount())
        );
    }
}
